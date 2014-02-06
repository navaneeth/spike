// This file is part of twist
package main

import (
	"code.google.com/p/goprotobuf/proto"
	"encoding/json"
	"errors"
	"fmt"
	"net"
	"os"
	"os/exec"
	"runtime"
	"sync"
	"time"
)

const (
	port             = ":8888"
	timeoutInSeconds = 30
)

type execution struct {
	tokens      []*token
	manifest    *manifest
	wg          sync.WaitGroup
	connections []net.Conn
}

func newExecution(manifest *manifest, tokens []*token) *execution {
	e := execution{manifest: manifest, tokens: tokens}
	return &e
}

// Looks for a runner configuration inside the runner directory
// finds the runner configuration matching to the manifest and executes the commands for the current OS
func (e *execution) startRunner() error {
	type runner struct {
		Name    string
		Command struct {
			Windows string
			Linux   string
			Darwin  string
		}
	}

	var r runner
	contents := readFileContents(fmt.Sprintf("runner/%s.json", e.manifest.Language))
	err := json.Unmarshal([]byte(contents), &r)
	if err != nil {
		return err
	}

	command := ""
	switch runtime.GOOS {
	case "windows":
		command = r.Command.Windows
		break
	case "darwin":
		command = r.Command.Darwin
		break
	default:
		command = r.Command.Linux
		break
	}

	cmd := exec.Command(command)
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	err = cmd.Start()
	if err != nil {
		return err
	}

	// Wait for the process to exit so we will get a detailed error message
	go func() {
		err := cmd.Wait()
		if err != nil {
			fmt.Printf("Runner exited with error: %s\n", err.Error())
		}
	}()

	return nil
}

func (e *execution) startAcceptingConnections() error {
	listener, err := net.Listen("tcp", port)
	if err != nil {
		return err
	}

	ready := make(chan bool)
	errChan := make(chan error)
	go func() {
		for {
			conn, err := listener.Accept()
			if err != nil {
				errChan <- err
				return
			}

			e.connections = append(e.connections, conn)
			ready <- true
			return
		}
	}()

	select {
	case <-ready:
		return nil
	case err := <-errChan:
		return err
	case <-time.After(timeoutInSeconds * time.Second):
		return errors.New("Timeout waiting for a runner")
	}
}

func (e *execution) start() error {
	err := e.startRunner()
	if err != nil {
		return err
	}

	err = e.startAcceptingConnections()
	if err != nil {
		return err
	}

	esr := &ExecutionStartingRequest{ScenarioFile: proto.String("sample.sc")}
	data, err := proto.Marshal(esr)
	if err != nil {
		return err
	}
	e.connections[0].Write(data)

	return nil
}

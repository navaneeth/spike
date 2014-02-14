// This file is part of twist
package main

import (
	"encoding/json"
	"fmt"
	"io"
	"os"
	"strings"
)

const (
	manifestFile = "manifest.json"
)

type manifest struct {
	Language string
}

func getProjectManifest() *manifest {
	contents := readFileContents(manifestFile)
	dec := json.NewDecoder(strings.NewReader(contents))

	var m manifest
	for {
		if err := dec.Decode(&m); err == io.EOF {
			break
		} else if err != nil {
			fmt.Printf("Failed to read: %s. %s\n", manifestFile, err.Error())
			os.Exit(1)
		}
	}

	return &m
}

func main() {
	if len(os.Args) != 2 {
		panic("Invalid args")
	}

	scenarioFile := os.Args[1]
	tokens, err := parse(readFileContents(scenarioFile))
	if se, ok := err.(*syntaxError); ok {
		fmt.Printf("%s:%d:%d %s\n", scenarioFile, se.lineNo, se.colNo, se.message)
		os.Exit(1)
	}

	manifest := getProjectManifest()

	_, err = startRunner(manifest)
	if err != nil {
		fmt.Printf("Failed to start a runner. %s\n", err.Error())
		os.Exit(1)
	}

	conn, err := acceptConnection()
	if err != nil {
		fmt.Printf("Failed to get a runner. %s\n", err.Error())
		os.Exit(1)
	}

	execution := newExecution(manifest, tokens, conn)
	err = execution.start()
	if err != nil {
		fmt.Printf("Execution failed. %s\n", err.Error())
		os.Exit(1)
	}

}

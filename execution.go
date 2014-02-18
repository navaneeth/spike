// This file is part of twist
package main

import (
	"code.google.com/p/goprotobuf/proto"
	"fmt"
	"io/ioutil"
	"net"
	"os"
)

type execution struct {
	tokens     []*token
	manifest   *manifest
	connection net.Conn
}

func newExecution(manifest *manifest, tokens []*token, conn net.Conn) *execution {
	e := execution{manifest: manifest, tokens: tokens, connection: conn}
	return &e
}

func (e *execution) startScenarioExecution() error {
	message := &Message{MessageType: Message_ExecutionStarting.Enum(),
		ExecutionStartingRequest: &ExecutionStartingRequest{ScenarioFile: proto.String("sample.sc")}}

	_, err := getResponse(e.connection, message)
	if err != nil {
		return err
	}

	return nil
}

func (e *execution) startStepExecution(token *token) (bool, error) {
	message := &Message{MessageType: Message_ExecuteStep.Enum(),
		ExecuteStepRequest: &ExecuteStepRequest{StepText: proto.String(token.value), Args: token.args}}

	fmt.Println(token.value)

	response, err := getResponse(e.connection, message)
	if err != nil {
		return false, err
	}

	if response.GetMessageType() == Message_ExecuteStepResponse {
		stepResponse := response.GetExecuteStepResponse()
		if stepResponse.GetPassed() != true {
			ioutil.WriteFile("/tmp/twist-screenshot.png", stepResponse.GetScreenShot(), 0644)
			fmt.Printf("\x1b[31;1m%s\n\x1b[0m", token.value)
			fmt.Printf("\x1b[31;1m%s\n\x1b[0m", stepResponse.GetErrorMessage())
			fmt.Printf("\x1b[31;1m%s\n\x1b[0m", stepResponse.GetStackTrace())
			return false, nil
		} else {
			fmt.Printf("\x1b[32;1m%s\n\x1b[0m", token.value)
		}
	}

	return true, nil
}

func (e *execution) stopScenarioExecution() error {
	message := &Message{MessageType: Message_ExecutionEnding.Enum(),
		ExecutionEndingRequest: &ExecutionEndingRequest{}}

	_, err := getResponse(e.connection, message)
	if err != nil {
		return err
	}

	return nil
}

func (e *execution) start() error {
	for _, token := range e.tokens {
		var err error
		quit := false

		switch token.kind {
		case typeScenario:
			err = e.startScenarioExecution()
			break
		case typeWorkflowStep:
			passed, err := e.startStepExecution(token)
			quit = !passed
			err = err
			break
		}

		if err != nil {
			fmt.Printf("Failed to execute step. %s\n", err.Error())
			os.Exit(1)
		}

		if quit {
			break
		}
	}

	return e.stopScenarioExecution()
}

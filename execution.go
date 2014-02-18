// This file is part of twist
package main

import (
	"code.google.com/p/goprotobuf/proto"
	"fmt"
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

func (e *execution) startStepExecution(token *token) error {
	message := &Message{MessageType: Message_ExecuteStep.Enum(),
		ExecuteStepRequest: &ExecuteStepRequest{StepText: proto.String(token.value), Args: token.args}}

	_, err := getResponse(e.connection, message)
	if err != nil {
		return err
	}

	return nil
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
		switch token.kind {
		case typeScenario:
			err = e.startScenarioExecution()
			break
		case typeWorkflowStep:
			err = e.startStepExecution(token)
			break
		}

		if err != nil {
			fmt.Printf("Failed to execute step. %s\n", err.Error())
			os.Exit(1)
		}
	}

	return e.stopScenarioExecution()
}

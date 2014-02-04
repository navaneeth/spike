// This file is part of twist
package main

import (
	"fmt"
	"io/ioutil"
	"os"
)

func main() {
	if len(os.Args) != 2 {
		panic("Invalid args")
	}

	scenarioFile := os.Args[1]
	bytes, err := ioutil.ReadFile(scenarioFile)
	if err != nil {
		fmt.Printf("Failed to read: %s. %s\n", scenarioFile, err.Error())
		os.Exit(1)
	}

	tokens, err := parse(string(bytes))
	if se, ok := err.(*syntaxError); ok {
		fmt.Printf("%s:%d:%d %s\n", scenarioFile, se.lineNo, se.colNo, se.message)
		os.Exit(1)
	}

	for _, element := range tokens {
		fmt.Println(element)
	}

}

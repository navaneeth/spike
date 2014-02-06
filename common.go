// Common functions shared across all files
package main

import (
	"fmt"
	"io/ioutil"
	"os"
)

func readFileContents(file string) string {
	bytes, err := ioutil.ReadFile(file)
	if err != nil {
		fmt.Printf("Failed to read: %s. %s\n", file, err.Error())
		os.Exit(1)
	}

	return string(bytes)
}

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

	bytes, err := ioutil.ReadFile(os.Args[1])
	if err != nil {
		panic(err)
	}

	tokens, err := parse(string(bytes))
	if se, ok := err.(*syntaxError); ok {
		fmt.Printf("%s\nat: %d\n", se.message, se.lineNo)
		os.Exit(1)
	}

	for _, element := range tokens {
		fmt.Println(element)
	}

}

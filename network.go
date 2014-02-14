package main

import (
	"bytes"
	"code.google.com/p/goprotobuf/proto"
	"io"
	"log"
	"net"
)

const (
	port             = ":8888"
	timeoutInSeconds = 30
)

// MessageId -> Callback
var pendingRequests = make(map[int64]chan<- *Message)

func handleConnection(conn net.Conn) {
	var buffer, remaining bytes.Buffer
	data := make([]byte, 8192)
	for {
		_, err := conn.Read(data)
		if err != nil {
			if err == io.EOF {
				log.Println("Client exited")
				return
			}
			log.Println(err.Error())
		}

		buffer.Write(data)

		messageLength, bytesRead := proto.DecodeVarint(buffer.Bytes())
		if messageLength <= uint64(buffer.Len()) {
			message := &Message{}
			err = proto.Unmarshal(buffer.Bytes()[bytesRead:messageLength+1], message)
			if err != nil {
				log.Printf("Failed to read proto message: %s\n", err.Error())
			} else {
				log.Println("Got a response")
				log.Println(message.GetMessageId())
				responseChannel := pendingRequests[*message.MessageId]
				responseChannel <- message
				delete(pendingRequests, *message.MessageId)

				remaining.Reset()
				remaining.Write(buffer.Bytes()[messageLength:])
				buffer.Reset()
				buffer.Write(remaining.Bytes())
			}
		}

		log.Printf("Message length is %d\n", messageLength)
		log.Printf("Bytes read is %d\n", bytesRead)

	}
}

func acceptConnection() (net.Conn, error) {
	listener, err := net.Listen("tcp", port)
	if err != nil {
		return nil, err
	}

	conn, err := listener.Accept()
	if err != nil {
		return nil, err
	}

	go handleConnection(conn)
	return conn, nil
}

// Sends the specified message and waits for a response
// This function blocks till it gets a response
// Each message gets a unique id and messages are prefixed with it's length
// encoded using protobuf'd varint format
func getResponse(conn net.Conn, message *Message) (*Message, error) {
	responseChan := make(chan *Message)
	messageId := getUniqueId()
	message.MessageId = &messageId
	pendingRequests[*message.MessageId] = responseChan

	data, err := proto.Marshal(message)
	if err != nil {
		delete(pendingRequests, *message.MessageId)
		return nil, err
	}
	data = append(proto.EncodeVarint(uint64(len(data))), data...)

	_, err = conn.Write(data)
	if err != nil {
		delete(pendingRequests, *message.MessageId)
		return nil, err
	}

	select {
	case response := <-responseChan:
		log.Println("Got a response")
		return response, nil
	}
}

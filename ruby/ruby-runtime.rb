require 'socket'
require 'protocol_buffers'

require_relative 'messages.pb'
require_relative 'executor'
require_relative 'message-processor'

HOST_NAME = 'localhost'
PORT = 8888

def dispatch_messages(socket)
	while (!socket.eof?)
	  len = message_length(socket)
	  data = socket.read len
	  message = Main::Message.parse(data)
	  handle_message(socket, message)
	end
end

def handle_message(socket, message)
	if (!MessageProcessor.is_valid_message(message)) 
		puts "Invalid message received"	
	else
		response = MessageProcessor.process_message message
		write_message(socket, response)
	end
end
	
def message_length(socket)
	ProtocolBuffers::Varint.decode socket	
end

def write_message(socket, message)
	serialized_message = message.to_s
	size = serialized_message.bytesize
	ProtocolBuffers::Varint.encode(socket, size)
	socket.write serialized_message
end



socket = TCPSocket.open(HOST_NAME, PORT)
load_steps()
dispatch_messages(socket)




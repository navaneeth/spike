require 'socket'
require 'protocol_buffers'
require_relative 'message-processor'
require_relative 'messages.pb'


HOST_NAME = 'localhost'
PORT = 8888

# msg = Main::Message.new(:messageType => Main::Message::MessageType::ExecutionStarting, :messageId => 1234)
# puts msg.messageType


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
	end
	response = MessageProcessor.process_message message
	write_message(socket, response)
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
dispatch_messages(socket)







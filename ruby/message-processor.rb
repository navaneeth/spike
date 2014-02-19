require_relative 'message-processor'
require_relative 'messages.pb'


class MessageProcessor
	
	def self.is_valid_message(message)
		return true	
	end

	def self.process_message(message)
		#returning dummy response to test
		execute_step = Main::ExecuteStepResponse.new(:passed => true, :recoverableError => true)
		Main::Message.new(:messageType => Main::Message::MessageType::ExecuteStepResponse, :executeStepResponse => execute_step, :messageId => message.messageId)
	end

end
require_relative 'messages.pb'
require_relative 'executor'
require 'tempfile'

EXECUTION_STARTING_REQUEST=0
EXECUTE_STEP_REQUEST=1
EXECUTE_STEP_RESPONSE=2
EXECUTION_ENDING=3
STEP_VALIDATE_REQUEST = 4
STEP_VALIDATE_RESPONSE = 5

class ExecuteStepProcessor
	
	def process(message)
		step_text = message.executeStepRequest.stepText
		args = message.executeStepRequest.args
		begin
			execute_step step_text, args
	 	rescue Exception => e
	 		return handle_step_failure message, e
	 	end
	 	handle_step_pass message
	end

	def handle_step_pass(message)
		execution_step_response = Main::ExecuteStepResponse.new(:passed => true)
		Main::Message.new(:messageType => EXECUTE_STEP_RESPONSE, :messageId => message.messageId, :executeStepResponse => execution_step_response) 
	end

	def handle_step_failure(message, exception)
		execution_step_response = Main::ExecuteStepResponse.new(:passed => false,
																:recoverableError => false,
																:errorMessage => exception.message,
																:stackTrace => exception.backtrace.join("\n"),
																:screenShot => screenshot_bytes)
		Main::Message.new(:messageType => EXECUTE_STEP_RESPONSE, :messageId => message.messageId, :executeStepResponse => execution_step_response) 
	end

	def screenshot_bytes
		file = File.open("#{Dir.tmpdir}/screenshot.png", "w+")
		`screencapture #{file.path}`
		bytes = file.read
		File.delete file
		return bytes	
	end
end

class ExecutionStartProcessor
	def process(message)
		return message
	end
end

class ExecutionEndProcessor
	def process(message)
		return message
	end
end

class StepValidationProcessor
	def process(message)
		step_text = message.stepValidateRequest.stepText
		step_validate_response = Main::StepValidateResponse.new(:isValid => is_valid_step(step_text))
		Main::Message.new(:messageType => STEP_VALIDATE_RESPONSE, :messageId => message.messageId, :stepValidateResponse => step_validate_response)
	end
end



class MessageProcessor

	@processors = Hash.new
	@processors[EXECUTION_STARTING_REQUEST] = ExecutionStartProcessor.new
	@processors[EXECUTE_STEP_REQUEST] = ExecuteStepProcessor.new
	@processors[EXECUTION_ENDING] = ExecutionEndProcessor.new
	@processors[STEP_VALIDATE_REQUEST] = StepValidationProcessor.new
	
	def self.is_valid_message(message)
		return @processors.has_key? message.messageType	
	end

	def self.process_message(message)
		@processors[message.messageType].process message
	end

end



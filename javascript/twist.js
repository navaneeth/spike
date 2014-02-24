
var steps = {}
var messageProcessor = {};

module.exports.step = function (steptext, callback) {
	steps[steptext] = callback;
}

require('./step_implementation')

var protoBuf = require("protobufjs");
var builder = protoBuf.loadProtoFile("../messages.proto");


var s = require('net').Socket();
s.connect(8888, 'localhost');

// function dispatchMessage(message) {

// }

var util = require('util');
var ByteBuffer = require("bytebuffer");
var message = builder.build("main.Message");

var ExecutionStarting = 0;
var ExecuteStep = 1;
var ExecuteStepResponse = 2;
var ExecutionEnding = 3;
var StepValidateRequest = 4;
var StepValidateResponse = 5;

messageProcessor[ExecutionStarting] = function (request) {
	return request;
};

messageProcessor[StepValidateRequest] = function (request) {
	var stepImpl = steps[request.stepValidateRequest.stepText];
	var response = null;
	if (stepImpl) {
		response = new message({messageId: request.messageId, messageType: StepValidateResponse, stepValidateResponse: {
				isValid: true
			}});
	}
	else {
		response = new message({messageId: request.messageId, messageType: StepValidateResponse, stepValidateResponse: {
				isValid: false
			}});
	}

	return response;
};

messageProcessor[ExecuteStep] = function (request) {
	var stepImpl = steps[request.executeStepRequest.stepText];
	var response = null;
	if (stepImpl) {
		try {
			var args = request.executeStepRequest.args;
			if (args.length == 0)
				stepImpl();
			else
				stepImpl.apply(this, args);

			response = new message({messageId: request.messageId, messageType: ExecuteStepResponse, executeStepResponse: {
				passed: true
			}});
		}
		catch (e) {
			response = new message({messageId: request.messageId, messageType: ExecuteStepResponse, executeStepResponse: {
				passed: false, recoverableError: false, errorMessage: e.message
			}});
			if (e.stack) {
				response.executeStepResponse.stackTrace = e.stack;
			}
		}
	}
	else {
		console.log("step not implemented");
	}

	return response;
}

messageProcessor[ExecutionEnding] = function (request) {
	s.end();
};

function writeResponse(response) {
	var encoded = response.encode().toBuffer();
	
	// finding the message length
	var messageLengthEncoded = new ByteBuffer();
	messageLengthEncoded.writeVarint64(encoded.length);
	var messageLengthEncodedBuffer = messageLengthEncoded.toBuffer();

	var bufferToWrite = new Buffer(messageLengthEncodedBuffer.length + encoded.length);
	messageLengthEncodedBuffer.copy(bufferToWrite);
	encoded.copy(bufferToWrite, messageLengthEncodedBuffer.length);

	s.write(bufferToWrite);
}

s.on('data', function(d){
	var bb = ByteBuffer.wrap(d)
	var messageLength = bb.readVarint64(0);

	// Take the remaining part as the actual message
	var data = d.slice(messageLength.length, messageLength.value.low + 1);
	
	var request = message.decode(data);
	if (request.messageType == ExecutionEnding) {
		writeResponse(request);
		s.end();
	}
	else {
		var response = messageProcessor[request.messageType](request);
		writeResponse(response);
	}
});







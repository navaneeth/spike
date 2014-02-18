import static main.Messages.Message;
import static main.Messages.Message.MessageType;
import static main.Messages.StepValidateResponse;

public class ValidateStepProcessor implements IMessageProcessor {

    @Override
    public Message process(Message message) {
        if (StepRegistry.contains(message.getStepValidateRequest().getStepText())) {
            return Message.newBuilder()
                    .setMessageId(message.getMessageId())
                    .setStepValidateResponse(StepValidateResponse.newBuilder().setIsValid(true).build())
                    .setMessageType(MessageType.StepValidateResponse)
                    .build();
        }
        else {
            return Message.newBuilder()
                    .setMessageId(message.getMessageId())
                    .setStepValidateResponse(StepValidateResponse.newBuilder().setIsValid(false).build())
                    .setMessageType(MessageType.StepValidateResponse)
                    .build();
        }
    }

}

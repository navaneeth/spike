import main.Messages;

public class ExecuteStepProcessor implements IMessageProcessor {
    @Override
    public Messages.Message process(Messages.Message message) {
        System.out.println(message.getExecuteStepRequest().getStepText());
        return message;
    }
}

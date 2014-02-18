import main.Messages;

public class ExecutionEndingProcessor implements IMessageProcessor {

    @Override
    public Messages.Message process(Messages.Message message) {
        System.out.println("Execution ending");
        return message;
    }

}

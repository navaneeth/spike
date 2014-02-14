import main.Messages;

public class ScenarioExecutionStartingProcessor implements IMessageProcessor {
    @Override
    public Messages.Message process(Messages.Message message) {
        System.out.println("Execution starting");
        System.out.println(message.getExecutionStartingRequest().getScenarioFile());
        return message;
    }
}

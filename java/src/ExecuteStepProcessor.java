import main.Messages;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecuteStepProcessor implements IMessageProcessor {

    private static Map<Method, Object> methodToClassInstanceMap = new HashMap<Method, Object>();
//    private Map<Class<?>, StringToPremitiveConverter>

    @Override
    public Messages.Message process(Messages.Message message) {
        process(message.getExecuteStepRequest());
        return message;
    }

    private void process(Messages.ExecuteStepRequest request) {
        if (StepRegistry.contains(request.getStepText())) {
            try {
                execute(request.getStepText(), request.getArgsList());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println(request.getStepText() + "----- not implemented");
        }
    }

    private void execute(String stepText, List<String> args) throws Exception {
        Method method = StepRegistry.get(stepText);
        Object classInstance = methodToClassInstanceMap.get(method);
        if (classInstance == null) {
            classInstance = Class.forName(method.getDeclaringClass().getName()).newInstance();
            methodToClassInstanceMap.put(method, classInstance);
        }

        if (args != null && args.size() > 0) {
            method.invoke(classInstance, args.toArray());
        }
        else {
            method.invoke(classInstance);
        }
    }
}

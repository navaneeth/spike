import com.google.protobuf.ByteString;
import main.Messages;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.Messages.*;
import static main.Messages.Message.MessageType;

public class ExecuteStepProcessor implements IMessageProcessor {

    private static Map<Method, Object> methodToClassInstanceMap = new HashMap<Method, Object>();

    @Override
    public Message process(Message message) {
        Message.Builder builder = process(message.getExecuteStepRequest());
        builder.setMessageId(message.getMessageId());
        return builder.build();
    }

    private Message.Builder process(ExecuteStepRequest request) {
        ExecuteStepResponse response;
        try {
            execute(request.getStepText(), request.getArgsList());
            response = ExecuteStepResponse.newBuilder().setPassed(true).build();
        } catch (Exception e) {
            ByteArrayOutputStream imageBytes = new ByteArrayOutputStream();
            try {
                BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                ImageIO.write(image, "png", imageBytes);
            } catch (Exception ex) {
                System.out.println("Screenshot is not available. " + ex.getMessage());
            }
            ExecuteStepResponse.Builder builder = ExecuteStepResponse.newBuilder().setPassed(false);
            builder.setErrorMessage(e.getCause().toString());
            if (e.getStackTrace() != null) {
                builder.setStackTrace(formatStackTrace(e.getCause().getStackTrace()));
            }
            if (imageBytes.size() > 0) {
                builder.setScreenShot(ByteString.copyFrom(imageBytes.toByteArray()));
            }
            builder.setRecoverableError(false);
            response = builder.build();
        }

        return Message.newBuilder().setMessageType(MessageType.ExecuteStepResponse).setExecuteStepResponse(response);
    }


    private String formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuffer output = new StringBuffer();
        for (StackTraceElement element : stackTrace) {
            output.append(element.toString());
            output.append("\n");
        }
        return output.toString();
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
        } else {
            method.invoke(classInstance);
        }
    }
}

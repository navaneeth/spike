import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import main.Messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;

import static main.Messages.Message.MessageType;
import static main.Messages.Message.MessageType.ExecuteStep;
import static main.Messages.Message.MessageType.ExecutionStarting;

public class Program {

    private static MessageLength getMessageLength(InputStream is) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int i = 0;
        while (true) {
            try {
                int read = is.read();
                if (read == -1) {
                    throw new IOException();
                }
                outputStream.write(read);
                i++;
                CodedInputStream cis = CodedInputStream.newInstance(outputStream.toByteArray());
                long size = cis.readRawVarint64();
                return new MessageLength(size, i);
            } catch (IOException e) {
                System.out.println("Failed to read from socket. " + e.getMessage());
                throw e;
            } catch (Exception e) {
            }
        }
    }

    private static byte[] toBytes(InputStream is, long messageSize) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i = 0; i < messageSize; i++) {
            outputStream.write(is.read());
        }

        return outputStream.toByteArray();
    }

    private static void writeMessage(Socket socket, Messages.Message message) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CodedOutputStream cos = CodedOutputStream.newInstance(stream);
        byte[] bytes = message.toByteArray();
        cos.writeRawVarint64(bytes.length);
        cos.flush();
        stream.write(bytes);
        socket.getOutputStream().write(stream.toByteArray());
        socket.getOutputStream().flush();
        System.out.println("Runner Wrote " + stream.toByteArray().length);
    }

    private static void dispatchMessages(Socket socket, HashMap<MessageType, IMessageProcessor> messageProcessors) throws Exception {
        InputStream inputStream = socket.getInputStream();
        while (!socket.isClosed()) {
            MessageLength messageLength = getMessageLength(inputStream);
            byte[] bytes = toBytes(inputStream, messageLength.length);
            Messages.Message message = Messages.Message.parseFrom(bytes);
            if (!messageProcessors.containsKey(message.getMessageType())) {
                System.out.println("Invalid message");
            } else {
                Messages.Message response = messageProcessors.get(message.getMessageType()).process(message);
                writeMessage(socket, response);
            }
        }
    }

    private static Socket connect() {
        Socket clientSocket;
        for (; ; ) {
            try {
                clientSocket = new Socket("localhost", 8888);
                break;
            } catch (Exception e) {
            }
        }

        return clientSocket;
    }

    public static void main(String[] args) throws Exception {
        HashMap<MessageType, IMessageProcessor> messageProcessors = new HashMap<MessageType, IMessageProcessor>() {{
            put(ExecutionStarting, new ScenarioExecutionStartingProcessor());
            put(ExecuteStep, new ExecuteStepProcessor());
        }};

        Socket socket = connect();
        dispatchMessages(socket, messageProcessors);
    }

    static class MessageLength {
        public long length;
        public int bytes;

        public MessageLength(long length, int bytes) {
            this.length = length;
            this.bytes = bytes;
        }
    }
}

import main.Messages;

import java.io.*;
import java.net.Socket;

public class Program {
    public static void main(String[] args) throws IOException {
        Socket clientSocket;
        for (;;) {
            try {
                clientSocket = new Socket("localhost", 8888);
                break;
            }
            catch (Exception e) {
            }
        }





        Messages.ExecutionStartingRequest executionStartingRequest = Messages.ExecutionStartingRequest.parseFrom(clientSocket.getInputStream());

        System.out.println(executionStartingRequest.getScenarioFile());



        System.out.println("Connected " + clientSocket.getPort());
    }
}

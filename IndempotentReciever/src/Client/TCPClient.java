    package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {

    public static void main(String args[]) {
        Socket connection = null;
        PrintWriter output = null;
        BufferedReader input = null;
        try {
            System.out.println("TCP Client Bank Started");
            connection = new Socket("localhost", 8080);
            output = new PrintWriter(connection.getOutputStream(),true);
            String inputMsg = "REQUEST;"+ connection.getInetAddress().getHostAddress()+";"+connection.getPort()+";create;1;1000";
            output.println(inputMsg);
            output.flush();
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String msg = input.readLine();
            System.out.println("Retorno do Servidor:"+msg);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
                output.close();
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("TCP Client Bank Finished");
    }
}

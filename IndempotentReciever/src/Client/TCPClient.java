package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {

    public static void main(String args[]) {
        Socket connection = null;
        ObjectOutputStream output = null;
        ObjectInputStream input = null;
        try {
            System.out.println("TCP Client Bank Started");
            connection = new Socket("localhost", 8080);
            output = new ObjectOutputStream(connection.getOutputStream());
            String inputMsg = "REQUEST;"+ connection.getInetAddress().getHostAddress()+";"+connection.getPort()+";create;1;1000";
            output.writeObject(inputMsg);
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            String msg = (String) input.readObject();
            System.out.println("Retorno do Servidor:"+msg);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
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

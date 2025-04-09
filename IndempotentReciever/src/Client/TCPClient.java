package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {

    public static void main(String args[]) {
        Socket conexao = null;
        ObjectOutputStream output = null;
        ObjectInputStream input = null;
        try {
            System.out.println("TCP Client Bank Started");
            conexao = new Socket("localhost", 8081);
            output = new ObjectOutputStream(conexao.getOutputStream());
            String inputMsg = "REQUEST\ncriar;1;1000";
            output.writeObject(inputMsg);
            output.flush();
            input = new ObjectInputStream(conexao.getInputStream());
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
                conexao.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("TCP Client Bank Finished");
    }
}

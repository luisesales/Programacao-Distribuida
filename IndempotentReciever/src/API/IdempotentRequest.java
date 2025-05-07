package API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class IdempotentRequestTCP {    
    public IdempotentRequestTCP(String msg){
        if(!msg.isBlank()){
            Socket connection = null;
            PrintWriter output = null;
            BufferedReader input = null;
            try {
                System.out.println("TCP Client Bank Started");
                connection = new Socket("localhost", 8080);
                output = new PrintWriter(connection.getOutputStream(),true);               
                output.println(msg);
                output.flush();
                input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String reply = input.readLine();
                System.out.println("Retorno do Servidor:"+reply);
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
            System.out.println("TCP Imdepotent Request Finished");
        }
    }
    
}

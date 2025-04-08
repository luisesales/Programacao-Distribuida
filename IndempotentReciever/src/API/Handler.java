package API;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Handler implements Runnable {

    private final Socket socket;
    private APIGateway gateway;
    private int instances = 0;

    public Handler(Socket socket, APIGateway gateway) {
        this.gateway = gateway;
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("\nHandler Started for " + this.socket);
        handleRequest(this.socket);
        System.out.println("Handler Terminated for " + this.socket + "\n");
    }

    public void handleRequest(Socket socket) {
        ObjectOutputStream output = null;
		ObjectInputStream input = null;
        try {            
            // Leitura do cabeçalho
            System.out.println("Lidando com a requisição");         
            input = new ObjectInputStream(socket.getInputStream());		
            output = new ObjectOutputStream(socket.getOutputStream());
			String msg = (String) input.readObject();
            System.out.println("\nmsg: "+msg);                        
            if (msg == null || msg.isEmpty()) {
                System.out.println("Requisição inválida recebida.");
                return;
            }                
            
            // Tomar ações com base no cabeçalho
            if (msg.equals("INIT SERVER")) {
                System.out.println("Iniciando a adição de servidor: " + socket);
                String name = "Instance " + instances++;
                gateway.addServer(socket.getInetAddress().getHostAddress(), socket.getPort(), name);
                System.out.println("Servidor adicionado: " + name);
            } else if (msg.equals("REQUEST")) {
                System.out.println("Processando requisição...");
                byte[] redirect = msg.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                byte[] reply = gateway.redirectRequest(redirect);                
                output.write(reply); // Envia a resposta ao cliente
                output.flush();
            } else {
                System.out.println("Método desconhecido: " + msg);
            }
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
			    socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
}
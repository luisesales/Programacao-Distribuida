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
            String reply;
            String[] request = msg.split(";");
            // Tomar ações com base no cabeçalho
            if (request[0].equals("INIT_SERVER")) {
                System.out.println("Iniciando a adição de servidor: " + socket);
                String name = "Instance " + instances++;
                gateway.addServer(request[1], Integer.parseInt(request[2]), name);                
                reply = "Servidor adicionado: " + name;
            } else if (request[0].equals("REQUEST")) {
                System.out.println("Processando requisição...");                
                reply = new String(gateway.redirectRequest(msg.getBytes(java.nio.charset.StandardCharsets.UTF_8)));                                
            } else {
                reply = "ERROR;Método desconhecido: " + msg;
                
                
            }
            System.out.println(reply);
            output.writeObject(reply); // Envia a resposta ao cliente
            output.flush();
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
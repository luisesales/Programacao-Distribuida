package API;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

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
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(socket.getOutputStream())
        ) {
            // Leitura do cabeçalho
            String headerLine = in.readLine();
            if (headerLine == null || headerLine.isEmpty()) {
                System.out.println("Requisição inválida recebida.");
                return;
            }

            StringTokenizer tokenizer = new StringTokenizer(headerLine);
            String method = tokenizer.nextToken();

            
            ByteArrayOutputStream bodyBuffer = new ByteArrayOutputStream();
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                bodyBuffer.write(line.getBytes());
                bodyBuffer.write("\n".getBytes());
            }
            byte[] bodyBytes = bodyBuffer.toByteArray();

            // Tomar ações com base no cabeçalho
            if (method.equals("INIT SERVER")) {
                System.out.println("Iniciando a adição de servidor: " + socket);
                String name = "Instance " + instances++;
                gateway.addServer(socket.getInetAddress().getHostAddress(), socket.getPort(), name);
                System.out.println("Servidor adicionado: " + name);
            } else if (method.equals("REQUEST")) {
                System.out.println("Processando requisição...");
                byte[] reply = gateway.redirectRequest(bodyBytes);
                out.write(reply); // Envia a resposta ao cliente
                out.flush();
            } else {
                System.out.println("Método desconhecido: " + method);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package API;

import Classes.IdempotencyStore;
import Classes.RequestStatus;
import Classes.WalEntry;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class HandlerTCP implements Runnable {

    private final Socket socket;
    private APIGateway gateway;
    private AtomicInteger instances;   
    private final String WAL_ID = "ba23a570-d27e-4e66-ad2d-d682d096ce7b"; 

    public HandlerTCP(Socket socket, APIGateway gateway, AtomicInteger instances) {
        this.gateway = gateway;
        this.socket = socket;
        this.instances = instances;        
    }

    @Override
    public void run() {
        System.out.println("\nHandler Started for " + this.socket);
        handleRequest(this.socket);
        System.out.println("Handler Terminated for " + this.socket + "\n");
    }

    private String processRequest(String msg, String[] request, WalEntry entry)
    {
        String reply = new String();
        try{        
        System.out.println("Processando requisição...");
        String newMsg;
        String removeTarget = request[1] + ";" + request[2] + ";";
        newMsg = msg.replace(removeTarget, "");
        newMsg = newMsg.trim();                                                                
        // Tentar redirecionar a requisição até obter uma resposta positiva
        int retries = 0;
        while (retries < 10) {
            reply = gateway.redirectRequestTCP(newMsg);
            if (reply.contains("operação realizada")) {
                entry.setStatus(RequestStatus.PROCESSED);
                break;
            } else {
                System.out.println("Falha ao processar requisição. Tentando novamente...");
                entry.setStatus(RequestStatus.FAILED);
                retries++;
                Thread.sleep(2000); // Aguarda 1 segundo antes de tentar novamente
                
            }
        }
        if(retries == 10){
            reply = "ERROR;Falha ao processar após 10 tentativas" + msg;
        }
        
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        return reply;
    }

    private void handleRequest(Socket socket) {
        ObjectOutputStream output = null;
        ObjectInputStream input = null;
        WalEntry entry = null;
        String reply = new String();
        try {
            // Leitura do cabeçalho
            System.out.println("Lidando com a requisição");
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
            String msg = (String) input.readObject();

            System.out.println("\nmsg: " + msg);
            if (msg == null || msg.isEmpty()) {
                System.out.println("Requisição inválida recebida.");
                reply = "ERROR;Requisição inválida recebida";         
            }
            else{            
                String[] request = msg.split(";");
                // Tomar ações com base no cabeçalho
                if (request[0].equals("INIT_SERVER")) {
                    System.out.println("Iniciando a adição de servidor: " + socket);
                    String name = "Instance " + instances.getAndIncrement();
                    gateway.addServer(request[1], Integer.parseInt(request[2]), name);
                    reply = "Servidor adicionado: " + name;

                } else if (request[0].equals("REQUEST")) {
                    String requestId = UUID.randomUUID().toString();                
                    entry = new WalEntry(requestId, msg);                
                    if (IdempotencyStore.isDuplicate(msg)) {
                        System.out.println("Requisição duplicada ignorada: " + msg);
                        reply = "Requisição duplicada ignorada: " + msg;                    
                    }
                    else{
                        reply = processRequest(msg, request, entry);   
                    }                                             
                }
                else {
                    reply = "ERROR;Método desconhecido: " + msg;
                }
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
                if (input != null) input.close();
                if (output != null) output.close();
                socket.close();
                if(entry != null) {
                    System.out.println(entry.getWalEntry());
                    IdempotencyStore.save(entry.getWalEntry());
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
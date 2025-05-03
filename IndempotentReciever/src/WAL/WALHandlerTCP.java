package WAL;

import Classes.IdempotencyStore;
import Classes.RequestStatus;
import Classes.WalEntry;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class WALHandlerTCP implements Runnable {

    private final Socket socket;
    private WALServer gateway;
    private AtomicInteger instances;    

    public WALHandlerTCP(Socket socket, WALServer gateway, AtomicInteger instances) {
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

    public void handleRequest(Socket socket) {
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
            String[] msgSplit = msg.split(";");
            System.out.println("\nmsg: " + msg);
            if (msg == null || msg.isEmpty()) {
                System.out.println("Requisição inválida recebida.");
                return;
            }
            else if (msg.equals("CLEAR")) {
                IdempotencyStore.clear();
                reply = "SUCCESS;Cache Limpa";
            }            
            else if(msgSplit[0].equals("WAL")){
               msg = msg.replace("WAL;"+msgSplit[1]+";", "");                                
               String requestId = UUID.randomUUID().toString();                               
               RequestStatus status = RequestStatus.fromCode(Integer.parseInt(msgSplit[1]));
               entry = new WalEntry(requestId, msg, status);          
               IdempotencyStore.add(msg);
               reply = "SUCCESS;Messagem Salva: " + msg; 
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
        } catch (NumberFormatException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } finally {
            try {
                if (input != null) input.close();
                if (output != null) output.close();
                socket.close();
                if(entry != null) {
                    if(entry.getStatus() == RequestStatus.PROCESSED)                        
                    System.out.println(entry.getWalEntry());
                    IdempotencyStore.save(entry.getWalEntry());
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
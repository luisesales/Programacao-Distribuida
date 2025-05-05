package WAL;

import Classes.IdempotencyStore;
import Classes.RequestStatus;
import Classes.WalEntry;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;

public class WALHandlerTCP implements Runnable {

    private final Socket socket;        

    public WALHandlerTCP(Socket socket) {        
        this.socket = socket;              
    }

    @Override
    public void run() {
        System.out.println("\nHandler Started for " + this.socket);
        handleRequest(this.socket);
        System.out.println("Handler Terminated for " + this.socket + "\n");
    }

    public void handleRequest(Socket socket) {
        PrintWriter output = null;
		BufferedReader input = null;
        WalEntry entry = null;     
        String reply = new String();
        try {
            // Leitura do cabeçalho
            System.out.println("Lidando com a requisição");
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream());
            String msg = input.readLine();
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
            else if (msgSplit[0].equals("WAL")){               
               String requestId = UUID.randomUUID().toString();                               
               RequestStatus status = RequestStatus.fromCode(Integer.parseInt(msgSplit[1]));
               System.out.println("Status = "+ status.getLabel());
               msg = msg.replace("WAL;"+msgSplit[1]+";", "").trim();
               System.out.println("\nmsg replaced: " + msg);                               
               entry = new WalEntry(requestId, msg, status);          
               System.out.println("Criei o Entry");
               IdempotencyStore.add(entry);
               reply = "SUCCESS;Messagem Salva: " + msg; 
            } else {
                reply = "ERROR;Método desconhecido: " + msg;
            }

            System.out.println(reply);
            output.println(reply); // Envia a resposta ao cliente
            output.flush();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
                    if(entry.getStatus() == RequestStatus.PROCESSED){                        
                        System.out.println(entry.getWalEntry());
                        IdempotencyStore.save(entry.getWalEntry());
                    }
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
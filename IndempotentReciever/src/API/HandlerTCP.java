package API;

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

    private String walRequest(String msg, WalEntry entry){
        Socket socket = null;
        PrintWriter output = null;
		BufferedReader input = null;
        String reply = new String();
        msg = "WAL;"+entry.getStatus().getCode()+";"+msg;
        try{
            socket = new Socket("localhost", 8081);
            output = new PrintWriter(socket.getOutputStream(),true);
            output.println(msg);
            output.flush();
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            reply = input.readLine();
            input.close();
            output.close();
            socket.close();
        }catch (IOException e) {
            e.printStackTrace();
            reply = "ERROR;Falha na conexão com WAL";
        }
        return reply;
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
                retries++;
                Thread.sleep(2000); // Aguarda 1 segundo antes de tentar novamente
                
            }
        }
        if(retries == 10){
            entry.setStatus(RequestStatus.FAILED);
            reply = "ERROR;Falha ao processar após 10 tentativas" + msg;
        }
        
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        return reply;
    }

    private void handleRequest(Socket socket) {
        PrintWriter output = null;
        BufferedReader input = null;
        WalEntry entry = null;
        String reply = new String();        
        String walMsg;
        try {
            // Leitura do cabeçalho
            System.out.println("Lidando com a requisição");
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream());
            String msg = input.readLine();
            String[] request = msg.split(";");
            System.out.println("\nmsg: " + msg);
            if (msg == null || msg.isEmpty()) {
                System.out.println("Requisição inválida recebida.");
                reply = "ERROR;Requisição inválida recebida";         
            }
            // Tomar ações com base no cabeçalho
            else if (request[0].equals("INIT_SERVER")) {
                System.out.println("Iniciando a adição de servidor: " + socket);
                String name = "Instance " + instances.getAndIncrement();
                gateway.addServer(request[1], Integer.parseInt(request[2]), name);
                reply = "Servidor adicionado: " + name;

            }else if (request[0].equals("REQUEST")) {
                String requestId = UUID.randomUUID().toString();                                    
                entry = new WalEntry(requestId, msg);    
                reply = walRequest(msg,entry);            
                String[] walReply = reply.split(";");
                if (walReply[0].equals("SUCCESS")) {
                    reply = processRequest(msg, request, entry); 
                    System.out.println(entry.getWalEntry());                                           
                }
                else{
                    System.out.println("Requisição duplicada ignorada: " + msg);                    
                }                                             
            }
            else if(request[0].equals(WAL_ID)){
                System.out.println("Processando Requisição não finalizada");
                String requestId = request[1];                       
                walMsg = msg.replace(request[0]+";", "").trim();                                        
                msg = msg.replace(request[0]+";"+request[1]+";", "").trim();                                
                entry = new WalEntry(requestId, msg);    
                request = msg.split(";");
                reply = processRequest(msg, request, entry);
                System.out.println(entry.getWalEntry());
                msg = walMsg;
            } 
            else {
                reply = "ERROR;Método desconhecido: " + msg;
            }            
            System.out.println(reply);
            output.println(reply); // Envia a resposta ao cliente
            output.flush();
            if(entry != null)
                walRequest(msg,entry);  
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null) input.close();
                if (output != null) output.close();
                socket.close();                                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
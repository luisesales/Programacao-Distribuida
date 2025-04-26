package API;

import Classes.IdempotencyStore;
import Classes.RequestStatus;
import Classes.WalEntry;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class HandlerUDP implements Runnable {

    private final DatagramPacket datagramPacket;
    private APIGateway gateway;
    private AtomicInteger instances;    

    public HandlerUDP(DatagramPacket packet, APIGateway gateway, AtomicInteger instances) {
        this.gateway = gateway;
        datagramPacket = packet;
        this.instances = instances;        
    }

    @Override
    public void run() {
        System.out.println("\nHandler Started for " + this.datagramPacket);
        handleRequest(this.datagramPacket);
        System.out.println("Handler Terminated for " + this.datagramPacket + "\n");
    }

    public void handleRequest(DatagramPacket packet) {
        ObjectOutputStream output = null;
        ObjectInputStream input = null;
        WalEntry entry = null;
        String reply = new String();
        try {
            // Leitura do cabeçalho
            System.out.println("Lidando com a requisição");                        
            String msg = new String(packet.getData());		
            System.out.println("\nmsg: " + msg);
            if (msg == null || msg.isEmpty()) {
                System.out.println("Requisição inválida recebida.");
                return;
            }
            
            String[] request = msg.split(";");
            // Tomar ações com base no cabeçalho
            if (request[0].equals("INIT_SERVER")) {
                System.out.println("Iniciando a adição de servidor: " + datagramPacket);
                String name = "Instance " + instances.getAndIncrement();
                gateway.addServer(packet.getAddress().getHostAddress(), packet.getPort(), name);
                reply = "Servidor adicionado: " + name;
            } else if (request[0].equals("REQUEST")) {
                String requestId = UUID.randomUUID().toString();                
                entry = new WalEntry(requestId, msg);                
                if (IdempotencyStore.isDuplicate(msg)) {
                    System.out.println("Requisição duplicada ignorada: " + msg);
                    return;
                }
                System.out.println("Processando requisição...");
                String newMsg;
                String removeTarget = request[1] + ";" + request[2] + ";";
                newMsg = msg.replace(removeTarget, "");
                newMsg = newMsg.trim();              
                // Adiciona a requisição ao WAL   
                System.out.println("Irei Salvar o Entry")       ;
                IdempotencyStore.save(entry.getWalEntry());  
                System.out.println("Salvei o Entry")                                           ;
                // Tentar redirecionar a requisição até obter uma resposta positiva
                int count = 0;
                while (count < 10) {
                    reply = gateway.redirectRequestUDP(newMsg);
                    if (reply.contains("operação realizada")) {
                        entry.setStatus(RequestStatus.PROCESSED);
                        break;
                    } else {
                        System.out.println("Falha ao processar requisição. Tentando novamente...");
                        entry.setStatus(RequestStatus.FAILED);                        
                        count++;
                        Thread.sleep(2000); // Aguarda 1 segundo antes de tentar novamente
                        
                    }
                }
                if(count == 10){
                    reply = "ERROR;Falha ao processar após 10 tentativas" + msg;
                }
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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
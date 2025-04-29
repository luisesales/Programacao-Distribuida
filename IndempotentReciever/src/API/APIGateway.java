package API;

import Classes.IdempotencyStore;
import Classes.Server;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class APIGateway {
    private Random rand = new Random();
    private int MAX_CONNECTIONS = 50;
    private static int ALIVE_TIMEOUT = 5000;
    private ArrayList<Server> AliveServers;
    private AtomicInteger instances = new AtomicInteger(0);
    //private static IdempotencyStore idempotency = new IdempotencyStore();
    public void addServer(String ip, int port, String name){
        AliveServers.add(new Server(ip,port,name));
    }
    public void removeServer(Server server){
        AliveServers.remove(server);
    }
    public ArrayList<Server> getAliveServers(){
        return new ArrayList<Server>(AliveServers);
    }

    public String redirectRequestTCP(String body){
        Server selected_server = AliveServers.get(rand.nextInt(AliveServers.size()));        
        try (Socket forwardSocket = new Socket(selected_server.getInetAddress(), selected_server.getPort())) {
            PrintWriter forwardOutput = new PrintWriter(forwardSocket.getOutputStream(), true);
            forwardOutput.println(body);                 
            forwardOutput.flush();
            
            // Recebe a resposta do segundo servidor            
            BufferedReader forwardInput = new BufferedReader(new InputStreamReader(forwardSocket.getInputStream()));
            String response = forwardInput.readLine();
            forwardInput.close();
            forwardOutput.close();
            forwardSocket.close();
            return response;

            // Envia a resposta de volta ao cliente
           
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*public String redirectRequestUDP(String body){
        Server selected_server = AliveServers.get(rand.nextInt(AliveServers.size()));  
        byte[] sendMessage = body.getBytes();     
        byte[] receivemessage = new byte[1024];         
        try {
            DatagramPacket forwardPacket = new DatagramPacket(sendMessage,sendMessage.length,selected_server.getInetAddress(), selected_server.getPort());
            DatagramSocket clientSocket = new DatagramSocket();
            clientSocket.send(forwardPacket);

            DatagramPacket receivepacket = new DatagramPacket(receivemessage, receivemessage.length);
			clientSocket.receive(receivepacket);
            // Envia a resposta de volta ao cliente
			return new String(receivepacket.getData());                                              
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    public APIGateway(){
        AliveServers = new ArrayList<Server>();        
    }

    private void RunGatewayTCP(APIGateway gateway){           
        try (ServerSocket server = new ServerSocket(8080, MAX_CONNECTIONS)) {
            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
            try{
                System.out.println("Gateway Listening to Requests");
                Heartbeat monitor = new Heartbeat(gateway,ALIVE_TIMEOUT);
                new Thread(monitor).start();
                while(true){                    
                    Socket remote = server.accept();
                    executor.execute(new HandlerTCP(remote,this,instances));                                        
                }
            }finally {
                executor.shutdown();
                server.close();
                System.out.println("Gateway terminating");
            }
        }catch (IOException e2) {
        e2.printStackTrace();
        }       
    }
   /* private void RunGatewayUDP(APIGateway gateway) {
        try (DatagramSocket serversocket = new DatagramSocket(8080)) {
            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
            try {
                System.out.println("Gateway Listening to Requests");
                Heartbeat monitor = new Heartbeat(gateway, ALIVE_TIMEOUT);
                new Thread(monitor).start();
                
                while (true) {                    
                    byte[] receivemessage = new byte[1024];
                    DatagramPacket remote = new DatagramPacket(receivemessage, receivemessage.length);
                    serversocket.receive(remote);
                    
                    System.out.println(new String(remote.getData(), 0, remote.getLength()));
                        
                    Future<String> futureReply = executor.submit(new HandlerUDP(remote, this, instances));
                    String reply = futureReply.get(); 
                                    
                    byte[] replymsg = reply.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(replymsg, replymsg.length,
                            remote.getAddress(), remote.getPort());
                    serversocket.send(sendPacket);
                }
            } finally {
                executor.shutdown();
                serversocket.close();
                System.out.println("Gateway terminating");
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }       
    }*/
    
    
    public static void main(String[] args) {
        APIGateway gateway = new APIGateway();
        gateway.RunGatewayTCP(gateway);        
        //gateway.RunGatewayUDP(gateway);
    }
}
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
    
    
    public static void main(String[] args) {
        APIGateway gateway = new APIGateway();
        gateway.RunGatewayTCP(gateway);        
    }
}
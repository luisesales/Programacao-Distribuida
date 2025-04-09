package API;

import Classes.Server;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;

public class APIGateway {
    private Random rand = new Random();
    private int MAX_CONNECTIONS = 50;
    private static int ALIVE_TIMEOUT = 5000;
    private ArrayList<Server> AliveServers;
    public void addServer(String ip, int port, String name){
        AliveServers.add(new Server(ip,port,name));
    }
    public void removeServer(Server server){
        AliveServers.remove(server);
    }
    public ArrayList<Server> getAliveServers(){
        return new ArrayList<Server>(AliveServers);
    }

    public byte[] redirectRequest(byte[] body){
        Server selected_server = AliveServers.get(rand.nextInt(AliveServers.size()));        
        try (Socket forwardSocket = new Socket(selected_server.getInetAddress(), selected_server.getPort())) {
            OutputStream forwardOutput = forwardSocket.getOutputStream();
            forwardOutput.write(body);
            forwardOutput.flush();
            
            // Recebe a resposta do segundo servidor
            byte[] buffer = new byte[4096];
            int bytesRead;
            InputStream forwardInput = forwardSocket.getInputStream();
            ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
            while ((bytesRead = forwardInput.read(buffer)) != -1) {
                responseBuffer.write(buffer, 0, bytesRead);
            }
            return responseBuffer.toByteArray();

            // Envia a resposta de volta ao cliente
           
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public APIGateway(){
        AliveServers = new ArrayList<Server>();        
    }

    private void RunGateway(APIGateway gateway){           
        try (ServerSocket server = new ServerSocket(8081, MAX_CONNECTIONS)) {
            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
            try{
                System.out.println("Gateway Listening to Requests");
                Heartbeat monitor = new Heartbeat(gateway,ALIVE_TIMEOUT);
                new Thread(monitor).start();
                while(true){
                    Socket remote = server.accept();
                    executor.execute(new Handler(remote,this));                                        
                }
            }finally {
                executor.shutdown();
            }
        }catch (IOException e2) {
        e2.printStackTrace();
        }
        System.out.println("Gateway terminating");
    }
    
    
    public static void main(String[] args) {
        APIGateway gateway = new APIGateway();
        gateway.RunGateway(gateway);        
    }
}
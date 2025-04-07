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

    public String redirectRequest(byte[] body){
        Server selected_server = AliveServers.get(rand.nextInt(AliveServers.size()));        
        try (Socket forwardSocket = new Socket(selected_server.getInetAddress(), selected_server.getPort())) {
            OutputStream forwardOutput = forwardSocket.getOutputStream();
            forwardOutput.write(body);
            forwardOutput.flush();

            // Recebe a resposta do segundo servidor
            InputStream forwardInput = forwardSocket.getInputStream();
            ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
            while ((bytesRead = forwardInput.read(buffer)) != -1) {
                responseBuffer.write(buffer, 0, bytesRead);
            }
            byte[] response = responseBuffer.toByteArray();

            // Envia a resposta de volta ao cliente
            clientOutput.write(response);
            clientOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public APIGateway(){
        AliveServers = new ArrayList<Server>();
        try(ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();ServerSocket Server = new ServerSocket(8080,MAX_CONNECTIONS);){            
            System.out.println("Gateway Listening to Requests");
            while(true){
                try{
                    Socket remote = Server.accept();

                    executor.execute(new Handler(remote,this));                    
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }catch (IOException e2) {
            e2.printStackTrace();
        }
        System.out.println("Gateway terminating");
    }
    
    
    public static void main(String[] args) {
        APIGateway gateway = new APIGateway();
        Heartbeat monitor = new Heartbeat(gateway,ALIVE_TIMEOUT);
        new Thread(monitor).start();

    }
}
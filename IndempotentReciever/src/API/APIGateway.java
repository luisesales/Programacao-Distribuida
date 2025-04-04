package API;

import Classes.Server;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.*;

public class APIGateway {
    private int MAX_CONNECTIONS = 50;
    private int TIMEOUT = 5000;
    private ArrayList<Server> AliveServers;
    public void AddServer(int ip, int port, String name){
        AliveServers.add(new Server(ip,port,name));
    }
    public void RemoveServer(Server server){
        AliveServers.remove(server);
    }

    private static void CheckAliveServers(){
        //TODO
    }

    public APIGateway(){
        AliveServers = new ArrayList<Server>();
        try(ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        ServerSocket Server = new ServerSocket(8080,MAX_CONNECTIONS);){            
            System.out.println("Gateway Listening to Requests");
            while(true){
                try{
                    Socket remote = Server.accept();

                    executor.execute(new Handler(remote,this));
                    CheckAliveServers();
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
        Heartbeat monitor = new Heartbeat(gateway,TIMEOUT);
        new Thread(monitor).start();

    }
}
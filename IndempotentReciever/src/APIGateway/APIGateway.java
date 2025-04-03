package APIGateway;

import java.io.*;
import java.net.*;
import java.util.List;

import Classes.Server;

public class APIGateway {
    private int TIMEOUT = 5000;
    private ArrayList<Server> AliveServers = new ArrayList<Server>(){} ;
    public void AddServer(int ip, int port){
        AliveServers.add(new Server(ip,port));
    }
    public void RemoveServer(Server server){
        AliveServers.remove(server);
    }
    private static void CheckAliveServers(){
        //TODO
    }

    public static void main(String[] args) {
        try {
            ServerSocket Gateway = new ServerSocket(8080);
            while(true){
                Socket remote = serverSocket.accept();

                new Thread(new Handler(remote,self)).start();
                CheckAliveServers();
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        System.out.println("UDP Bank server terminating");
    }
}
package API;

import Classes.Server;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;



public class Heartbeat implements Runnable {
    private APIGateway gateway;
    private static int ALIVE_TIMEOUT;

    public Heartbeat(APIGateway gateway,int timeout){
        this.gateway = gateway;
        ALIVE_TIMEOUT = timeout;
    }

   @Override
    public void run() {
        while (true) { 
            ArrayList<Server> servers = gateway.getAliveServers();
            
            System.out.println("Checking Alive servers...");

            for (Server server : servers) {              
                System.out.println("Server " + server.getPort() + " is alive... until now");  
                heartbeat(server);                
            }

            try {
                Thread.sleep(ALIVE_TIMEOUT);
            } catch (InterruptedException e) {                
                e.printStackTrace();
            }
        }
    }

    public void heartbeat(Server server){
        try{
            Socket socket = new Socket(server.getInetAddress(), server.getPort());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("HEARTBEAT");            
            out.flush();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Error broadcasting heartbeat on server" + server.getName() + " on port " + server.getPort() + ": " + e.getMessage());
            e.printStackTrace();
            gateway.removeServer(server);
            System.out.println("Server " + server.getName() + " is down. Removing from list.");
        }
    }    
}


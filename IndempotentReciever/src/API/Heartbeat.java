package API;

import Classes.Server;



public class Heartbeat implements Runnable {
    private APIGateway gateway;
    private int TIMEOUT;

    public Heartbeat(APIGateway gateway,int timeout){
        this.gateway = gateway;
        TIMEOUT = timeout;
    }

    @Override
    public void run(){
        while (true) { 
            try {
                Thread.sleep(TIMEOUT);
                gateway.RemoveServer(new Server(10, 1 , "Instance 1"));
                System.out.println("Server:" + 1 +" has died");
            } catch (InterruptedException e) {
                System.out.println("Heartbeat Interrupted");
                break;
            }
        }
    }    
}

package WAL;

import Classes.IdempotencyStore;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.*;

public class WALServer {    
    private int MAX_CONNECTIONS = 50; 
    private final String WAL_ID = "ba23a570-d27e-4e66-ad2d-d682d096ce7b";       

    public void RunRequests(ArrayList<String> requests){        
        Boolean successProccess = true;
        Socket connection = null;
        ObjectOutputStream output = null;
        ObjectInputStream input = null;         
        for(String request : requests ){
            try {                
                connection = new Socket("localhost", 8080);
                output = new ObjectOutputStream(connection.getOutputStream());
                String inputMsg = WAL_ID +";"+ request;                
                output.writeObject(inputMsg);
                output.flush();
                input = new ObjectInputStream(connection.getInputStream());
                String msg = (String) input.readObject();
                System.out.println("Retorno do Gateway:"+msg);                
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                successProccess = false;
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    input.close();
                    output.close();
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Request: "+ request + " has been sent");
        }        
        if(successProccess){
            IdempotencyStore.clearCache();
        }        
    }

    private void RunWALTCP(WALServer wal){           
        try (ServerSocket server = new ServerSocket(8081, MAX_CONNECTIONS)) {
            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
            try{
                IdempotencyStore.load();
                RunRequests(IdempotencyStore.getCache());
                System.out.println("WAL Listening to Requests");                
                while(true){                    
                    Socket remote = server.accept();
                    executor.execute(new WALHandlerTCP(remote));                                        
                }
            }finally {
                executor.shutdown();
                server.close();
                IdempotencyStore.clearCache();
                System.out.println("WAL terminating");
            }
        }catch (IOException e2) {
        e2.printStackTrace();
        }       
    }
    
    
    public static void main(String[] args) {
        WALServer wal = new WALServer();
        wal.RunWALTCP(wal);                
    }
}


package WAL;

import Classes.IdempotencyStore;
import Classes.WalEntry;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class WALServer {    
    private int MAX_CONNECTIONS = 50; 
    private final String WAL_ID = "ba23a570-d27e-4e66-ad2d-d682d096ce7b";   
    public AtomicBoolean preparation = new AtomicBoolean(true);  
    
    public void RunRequests(ArrayList<WalEntry> requests){ 
        synchronized(this){       
            preparation.set(false);
            Boolean successProccess = true;
            Socket connection = null;
            PrintWriter output = null;
            BufferedReader input = null;  
            for(WalEntry request : requests ){
                try {                
                    connection = new Socket("localhost", 8080);
                    output = new PrintWriter(connection.getOutputStream(),true);
                    String inputMsg = WAL_ID +";"+ request.getId()+";"+request.getPayload();                
                    output.println(inputMsg);
                    output.flush();
                    input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String msg = input.readLine();                
                    System.out.println("Retorno do Gateway:"+msg);                                
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    successProccess = false;        
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
    }

    private void RunWALTCP(WALServer wal){           
        try (ServerSocket server = new ServerSocket(8081, MAX_CONNECTIONS)) {
            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
            try{           
                IdempotencyStore.clearCache();                       
                System.out.println("WAL Listening to Requests");                
                while(true){                    
                    Socket remote = server.accept();
                    executor.execute(new WALHandlerTCP(remote,this));                                        
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


    package WAL;

    import Classes.IdempotencyStore;
    import Classes.RequestStatus;
    import Classes.WalEntry;
    import java.io.*;
    import java.net.Socket;
    import java.net.UnknownHostException;

    public class WALHandlerTCP implements Runnable {

        private final Socket socket;
        private final WALServer server;   
        private final IdempotencyStore store;     

        public WALHandlerTCP(Socket socket, WALServer server, IdempotencyStore store) {        
            this.socket = socket;              
            this.server = server;
            this.store = store;
        }

        @Override
        public void run() {
            System.out.println("\nHandler Started for " + this.socket);
            if(server.preparation.get() == true){
                server.RunRequests(store.load());
            }
            handleRequest(this.socket);
            System.out.println("Handler Terminated for " + this.socket + "\n");
        }

        

        public void handleRequest(Socket socket) {
            PrintWriter output = null;
            BufferedReader input = null;
            WalEntry entry = null;     
            String reply = new String();
            int selectedServer = 0;
            try {
                // Leitura do cabeçalho
                System.out.println("Lidando com a requisição");
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream());
                String msg = input.readLine();
                String[] msgSplit = msg.split(";");
                System.out.println("\nmsg: " + msg);
                if (msg == null || msg.isEmpty()) {
                    System.out.println("Requisição inválida recebida.");
                    return;
                }
                else if (msg.equals("CLEAR")) {
                    store.clear();
                    reply = "SUCCESS;Cache Limpa";
                }
                else if (msgSplit[0].equals("WAL")){    
                    RequestStatus status = RequestStatus.fromCode(Integer.parseInt(msgSplit[1]));
                    selectedServer = Integer.parseInt(msgSplit[2]);
                    String requestId;     
                    boolean checkDup = true;      
                    System.out.println("Request recebido:"+msg);
                    if(msgSplit[3].equals("REQUEST")){                             
                            msg = msg.replace("WAL;"+msgSplit[1]+";"+msgSplit[2]+";", "").trim();
                            requestId = store.getId(msg,selectedServer);
                            if(store.isDuplicate(selectedServer,requestId,status)){
                                reply = "ERROR;Messagem Duplicata: " + msg;                                                
                                checkDup = false; 
                            }                                                            
                    }                                                 
                    else{
                            System.out.println("Mensagem de Idempotencia detectada: "+msg );
                            requestId = msgSplit[3].trim();
                            msg = msg.replace("WAL;"+msgSplit[1]+";"+msgSplit[2]+";"+msgSplit[3]+";", "").trim();                                        
                    }               
                    System.out.println("Status = "+ status.getLabel());                    
                    System.out.println("\nmsg replaced: " + msg);  
                    if(checkDup){
                            entry = new WalEntry(requestId, msg, status);          
                            System.out.println("Criei o Entry");
                            //store.add(entry);
                            store.save(entry,selectedServer);
                            reply = "SUCCESS;Messagem Salva: " + msg; 
                    }
                } else {
                    reply = "ERROR;Método desconhecido: " + msg;
                }

                System.out.println(reply);
                output.println(reply); // Envia a resposta ao cliente
                output.flush();
                input.close();
                output.close();
                socket.close();                                
                if(entry != null) {                                        
                    System.out.println(entry.getWalEntry());                    
                    //store.simpleSave(entry,selectedServer);                    
                } 
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();    
            }       
            
        }
    }
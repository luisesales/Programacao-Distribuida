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

        public WALHandlerTCP(Socket socket, WALServer server) {        
            this.socket = socket;              
            this.server = server;
        }

        @Override
        public void run() {
            System.out.println("\nHandler Started for " + this.socket);
            if(server.preparation.get() == true){
                server.RunRequests(IdempotencyStore.load());
            }
            handleRequest(this.socket);
            System.out.println("Handler Terminated for " + this.socket + "\n");
        }

        

        public void handleRequest(Socket socket) {
            PrintWriter output = null;
            BufferedReader input = null;
            WalEntry entry = null;     
            String reply = new String();
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
                    IdempotencyStore.clear();
                    reply = "SUCCESS;Cache Limpa";
                }
                else if (msgSplit[0].equals("WAL")){    
                    RequestStatus status = RequestStatus.fromCode(Integer.parseInt(msgSplit[1]));
                    String requestId;     
                    boolean checkDup = true;      
                    System.out.println("Request recebido:"+msg);
                    if(msgSplit[2].equals("REQUEST")){                             
                            msg = msg.replace("WAL;"+msgSplit[1]+";", "").trim();
                            requestId = IdempotencyStore.getId(msg);
                            if(IdempotencyStore.isDuplicate(msg) && status == RequestStatus.PENDING){
                                reply = "ERROR;Messagem Duplicata: " + msg;                                                
                                checkDup = false; 
                            }                                                            
                    }                                                 
                    else{
                            System.out.println("Mensagem de Idempotencia detectada: "+msg );
                            requestId = msgSplit[2];
                            msg = msg.replace("WAL;"+msgSplit[1]+";"+msgSplit[2]+";", "").trim();                                        
                    }               
                    System.out.println("Status = "+ status.getLabel());
                    
                    System.out.println("\nmsg replaced: " + msg);  
                    if(checkDup){
                            entry = new WalEntry(requestId, msg, status);          
                            System.out.println("Criei o Entry");
                            IdempotencyStore.add(entry);
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
                    IdempotencyStore.save(entry);                    
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
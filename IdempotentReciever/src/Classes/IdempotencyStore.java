    package Classes;

    import java.io.*;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.Map;
    import java.util.Set;
    import java.util.UUID;
    import java.util.concurrent.ConcurrentHashMap;
    import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

    public class IdempotencyStore {
        private final int WAL_THRESHOLD = 250;
        private Map<Integer,AtomicInteger> WAL_REQUESTS_COUNT = new HashMap<>();
        private final String CACHE_FILE = "cache.log";
        private final Map<Integer,String> WAL_FILES = Map.of(
            0, "wal0.log", 
            1, "wal1.log",
            2, "wal2.log",
            3, "wal3.log"
        );

        private final Set<String> pendingRequests = ConcurrentHashMap.newKeySet();
        private final Set<String> finishedRequests = ConcurrentHashMap.newKeySet();

        private final Map<Integer,ConcurrentHashMap<String,WalEntry>> WAL_REQUESTS_SERVERS;    
        private final Semaphore semaphore = new Semaphore(25,true);
        public IdempotencyStore()
         {                      
            WAL_REQUESTS_COUNT.put(0, new AtomicInteger(0));
            WAL_REQUESTS_COUNT.put(1, new AtomicInteger(0));
            WAL_REQUESTS_COUNT.put(2, new AtomicInteger(0));
            WAL_REQUESTS_COUNT.put(3, new AtomicInteger(0));
            Map<Integer, ConcurrentHashMap<String, WalEntry>> tempMap = new HashMap<>();
            tempMap.put(0, new ConcurrentHashMap<>());
            tempMap.put(1, new ConcurrentHashMap<>());
            tempMap.put(2, new ConcurrentHashMap<>());
            tempMap.put(3, new ConcurrentHashMap<>());
            
            WAL_REQUESTS_SERVERS = Map.copyOf(tempMap); 
            // Carrega WAL ao iniciar
            try (BufferedReader reader = new BufferedReader(new FileReader(CACHE_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();      
                    pendingRequests.add(line);                           
                }
            } catch (IOException e) {
                System.out.println("Nenhum WAL existente, iniciando novo.");

            }
        }

        public String getId(String request,int file){
            for(WalEntry entry : WAL_REQUESTS_SERVERS.get(file).values()){                                          
                if(entry.getPayload().equals(request)  && entry.getStatus() == RequestStatus.PENDING){
                    return entry.getId();
                }
            }
            return UUID.randomUUID().toString();
        }

        public boolean isDuplicate(int file,String id) {
            return WAL_REQUESTS_SERVERS.get(file).containsKey(id);
        }

        public ArrayList<String> getCache(){
            return new ArrayList<>(pendingRequests);
        }

        public ArrayList<WalEntry> load(){
            // Carrega WAL ao iniciar
            ArrayList<WalEntry>     pendingEntries = new ArrayList<WalEntry>();
            int i = 0;            
            for(String WAL_FILE : WAL_FILES.values()){
                try (BufferedReader reader = new BufferedReader(new FileReader(WAL_FILE))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();      
                        if(line.isBlank()) 
                            break;          
                        WalEntry entry = WalEntry.getWalEntry(line);                             
                        if (entry.getStatus() == RequestStatus.PENDING || entry.getStatus() == RequestStatus.FAILED) { 
                            pendingRequests.add(entry.getPayload());
                            pendingEntries.add(entry);
                        }                                
                    }
                } catch (IOException e) {
                    System.out.println("Nenhum WAL existente para o servidor "+ i++ +", iniciando novo.");

                }            
            }
            return pendingEntries;
        }

        public void simpleAdd(WalEntry entry){
            pendingRequests.add(entry.getPayload());
        }

        public void add(String payload){
            if (pendingRequests.add(payload)) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(CACHE_FILE, true))) {
                    writer.write(payload);
                    writer.newLine();                
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void add(WalEntry entry){
            if (pendingRequests.add(entry.getPayload())) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(CACHE_FILE, true))) {
                    writer.write(entry.getPayload());
                    writer.newLine();                
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        

        public void simpleRemove(WalEntry entry){        
           pendingRequests.remove(entry.getPayload());
        }

        public void simpleRemove(String payload){        
            pendingRequests.remove(payload);
         }

        public void remove(String payload){                
            if (pendingRequests.remove(payload)) {            
                try {
                    File inputFile = new File(CACHE_FILE);
                    File tempFile = new File("temp_" + CACHE_FILE);

                    try (
                        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
                    ) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if(line.isBlank()) break;                        
                            if (line.equals(payload)) {
                                writer.write(""); // Substitui linha
                            } else {
                                writer.write(line); // Mantém linha original
                            }
                            writer.newLine();
                        }
                    }                
                    // Substitui o arquivo antigo pelo novo
                    if (!inputFile.delete()) {
                        throw new IOException("Não foi possível deletar o arquivo original.");
                    }
                    if (!tempFile.renameTo(inputFile)) {
                        throw new IOException("Não foi possível renomear o arquivo temporário.");
                    }                
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void remove(WalEntry entry){        
            if (pendingRequests.remove(entry.getPayload())) {                        
                try {
                    File inputFile = new File(CACHE_FILE);
                    File tempFile = new File("temp_" + CACHE_FILE);

                    try (
                        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
                    ) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if(line.isBlank()) break;                        
                            if (line.equals(entry.getPayload())) {
                                writer.write(""); // Substitui linha
                            } else {
                                writer.write(line); // Mantém linha original
                            }
                            writer.newLine();
                        }
                    }                
                    
                    // Substitui o arquivo antigo pelo novo
                    if (!inputFile.delete()) {
                        throw new IOException("Não foi possível deletar o arquivo original.");
                    }
                    if (!tempFile.renameTo(inputFile)) {
                        throw new IOException("Não foi possível renomear o arquivo temporário.");
                    }
                    System.out.println("deletei o arquivo temp");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        public void clearCache(){
            pendingRequests.clear();
            try (FileWriter writer = new FileWriter(CACHE_FILE, false)) {            
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void clearRequests(){
            finishedRequests.clear();
            for(String file : WAL_FILES.values())
                try (FileWriter writer = new FileWriter(file, false)) {            
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        public void clearFile(int file){                        
                try (FileWriter writer = new FileWriter(WAL_FILES.get(file), false)) {            
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        public void clear(){
            clearCache();
            clearRequests();
        }

        public void save(WalEntry entry,int file) {
            try {                                    
                if (entry.getStatus() != RequestStatus.PENDING) {
                    simpleRemove(entry); // Supondo que remove também está sincronizado ou é seguro
                }                
                semaphore.acquire(); // Entrando na seção crítica
                if (finishedRequests.add(entry.getWalEntry())) {
                    appendToWAL(entry.getWalEntry(),file);
                } else {
                    replaceInFile(entry.getId(), entry.getWalEntry(),file);
                }
    
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                semaphore.release(); // Saindo da seção crítica
            }
        }

        public void simpleSave(WalEntry entry,int file){
            try{
                semaphore.acquire();
                final ConcurrentHashMap<String,WalEntry> selectedLog = WAL_REQUESTS_SERVERS.get(file);
                AtomicInteger selectedCount = WAL_REQUESTS_COUNT.get(file);
                if(selectedLog.containsKey(entry.getId())){                    
                    selectedLog.replace(entry.getId(), entry);
                    simpleRemove(entry);
                }
                else{
                    selectedLog.put(entry.getId(), entry);                
                }                
                if(selectedCount.incrementAndGet() == WAL_THRESHOLD){                    
                    simpleAppendToWAL(file);                                        
                }            
                selectedCount.set(selectedCount.get()%WAL_THRESHOLD);            
                semaphore.release();
                }catch (InterruptedException e){
                    e.printStackTrace();
            }
        }
    
        public void save(String request,int file) {
            try {                                    
                semaphore.acquire(); // Entrando na seção crítica
                WalEntry entry = WalEntry.getWalEntry(request);
                simpleRemove(entry);
                /*if (entry.getStatus() != RequestStatus.PENDING) {
                    
                } */               
                
                if (finishedRequests.add(entry.getWalEntry())) {
                    appendToWAL(entry.getWalEntry(),file);
                } else {
                    replaceInFile(entry.getId(), entry.getWalEntry(),file);
                }
    
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                semaphore.release(); // Saindo da seção crítica
            }
        }
        private void simpleAppendToWAL(int file) { 
            //System.out.println("Vou Adicionar ao WAL "+file);
            clearFile(file);                  
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(WAL_FILES.get(file), true))) {
                for(WalEntry entry : WAL_REQUESTS_SERVERS.get(file).values()){
                    writer.write(entry.getWalEntry());
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
        private   void appendToWAL(String walEntry, int file) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(WAL_FILES.get(file), true))) {
                writer.write(walEntry);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
        private void replaceInFile(String targetRequestId, String newEntry, int file) {
            String selectedFile = WAL_FILES.get(file);
            File inputFile = new File(selectedFile);
            File tempFile = new File("temp_" + selectedFile);
        
            try (
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
            ) {
                String line;
                boolean replaced = false;
        
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) continue;
        
                    WalEntry existing = WalEntry.getWalEntry(line);
        
                    if (existing.getId().equals(targetRequestId)) {
                        writer.write(newEntry); // Substitui entrada
                        replaced = true;
                    } else {
                        writer.write(line); // Mantém as outras
                    }
                    writer.newLine();
                }
        
                // Se não encontrou o ID, adiciona no final (opcional)
                if (!replaced) {
                    writer.write(newEntry);
                    writer.newLine();
                }
        
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        
            // Substitui o arquivo original
            try {
                if (!inputFile.delete()) {
                    throw new IOException("Erro ao deletar arquivo original.");
                }
                if (!tempFile.renameTo(inputFile)) {
                    throw new IOException("Erro ao renomear arquivo temporário.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
                
    }    

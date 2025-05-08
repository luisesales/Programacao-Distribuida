    package Classes;

    import java.io.*;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.Set;
    import java.util.UUID;
    import java.util.concurrent.ConcurrentHashMap;
    import java.util.concurrent.Semaphore;


    public class IdempotencyStore {
        private static final String CACHE_FILE = "cache.log";
        private static final Map<Integer,String> WAL_FILES = Map.of(
            0, "wal0.log", 
            1, "wal1.log",
            2, "wal2.log",
            3, "wal3.log"
        );
        private static final String WAL0_FILE = "wal0.log";
        private static final String WAL1_FILE = "wal1.log";
        private static final String WAL2_FILE = "wal2.log";
        private static final String WAL3_FILE = "wal3.log";

        private static final Set<String> pendingRequests = ConcurrentHashMap.newKeySet();
        private static final Set<String> finishedRequests = ConcurrentHashMap.newKeySet();
        private static final Semaphore semaphore = new Semaphore(1);
        static{      
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

        public static String getId(String request){
            for(String finishedrequest : finishedRequests){            
                WalEntry entry = WalEntry.getWalEntry(finishedrequest);              
                if(entry.getPayload().equals(request)  && entry.getStatus() == RequestStatus.PENDING){
                    return entry.getId();
                }
            }
            return UUID.randomUUID().toString();
        }

        public static boolean isDuplicate(String request) {
            return pendingRequests.contains(request);
        }

        public static ArrayList<String> getCache(){
            return new ArrayList<>(pendingRequests);
        }

        public static ArrayList<WalEntry> load(){
            // Carrega WAL ao iniciar
            ArrayList<WalEntry> pendingEntries = new ArrayList<WalEntry>();
            for(String WAL_FILE : WAL_FILES.values())
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
                System.out.println("Nenhum WAL existente, iniciando novo.");

            }
            return pendingEntries;
        }

        public static void add(String payload){
            if (pendingRequests.add(payload)) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(CACHE_FILE, true))) {
                    writer.write(payload);
                    writer.newLine();                
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public static void add(WalEntry entry){
            if (pendingRequests.add(entry.getPayload())) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(CACHE_FILE, true))) {
                    writer.write(entry.getPayload());
                    writer.newLine();                
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public static void simpleRemove(WalEntry entry){        
           pendingRequests.remove(entry.getPayload());
        }

        public static void simpleRemove(String payload){        
            pendingRequests.remove(payload);
         }

        public static void remove(String payload){        
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

        public static void remove(WalEntry entry){        
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


        public static void clearCache(){
            pendingRequests.clear();
            try (FileWriter writer = new FileWriter(CACHE_FILE, false)) {            
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void clearRequests(){
            finishedRequests.clear();
            for(String file : WAL_FILES.values())
                try (FileWriter writer = new FileWriter(file, false)) {            
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        public static void clear(){
            clearCache();
            clearRequests();
        }

        public static  void save(WalEntry entry,int file) {
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
    
        public static  void save(String request,int file) {
            try {                    
                
                WalEntry entry = WalEntry.getWalEntry(request);
                if (entry.getStatus() != RequestStatus.PENDING) {
                    simpleRemove(entry);
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
    
        private static void appendToWAL(String walEntry, int file) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(WAL_FILES.get(file), true))) {
                writer.write(walEntry);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
        private static void replaceInFile(String targetRequestId, String newEntry, int file) {
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

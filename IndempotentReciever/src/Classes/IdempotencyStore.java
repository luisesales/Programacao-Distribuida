package Classes;

import java.io.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class IdempotencyStore {
    private static final String CACHE_FILE = "cache.log";
    private static final String WAL_FILE = "wal.log";
    private static final Set<String> pendingRequests = ConcurrentHashMap.newKeySet();
    private static final Set<String> finishedRequests = ConcurrentHashMap.newKeySet();
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

    public static boolean isDuplicate(String request) {
        return pendingRequests.contains(request);
    }

    public static ArrayList<String> getCache(){
        return new ArrayList<>(pendingRequests);
    }

    public static ArrayList<WalEntry> load(){
        // Carrega WAL ao iniciar
        ArrayList<WalEntry> pendingEntries = new ArrayList<WalEntry>();
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
        try (FileWriter writer = new FileWriter(WAL_FILE, false)) {            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clear(){
        clearCache();
        clearRequests();
    }

    public static void save(String request) {
        if (finishedRequests.add(WalEntry.getWalEntry(request).getWalEntry())) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(WAL_FILE, true))) {
                writer.write(request);
                writer.newLine();                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            remove(WalEntry.getWalEntry(request).getPayload());
            try {
                File inputFile = new File(WAL_FILE);
                File tempFile = new File("temp_" + WAL_FILE);

                try (
                    BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                    BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
                ) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if(line.isBlank()) break;
                        String existingPayload = WalEntry.getWalEntry(line).getPayload();
                        if (existingPayload.equals(WalEntry.getWalEntry(request).getPayload())) {
                            writer.write(request); // Substitui linha
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
    public static void save(WalEntry entry) {
        if (finishedRequests.add(entry.getWalEntry())) {
            System.out.println("Adicionei no finishedRequests set");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(WAL_FILE, true))) {
                System.out.println("Vou Escrever");
                writer.write(entry.getWalEntry());
                System.out.println("Escrevi");
                writer.newLine();                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }        
        else {
            remove(entry);
            try {
                File inputFile = new File(WAL_FILE);
                File tempFile = new File("temp_" + WAL_FILE);

                try (
                    BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                    BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
                ) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if(line.isBlank()) break;
                        String existingPayload = WalEntry.getWalEntry(line).getPayload();
                        if (existingPayload.equals(WalEntry.getWalEntry(entry.getWalEntry()).getPayload())) {
                            writer.write(entry.getWalEntry()); // Substitui linha
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
}    

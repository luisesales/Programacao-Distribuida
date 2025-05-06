package Classes;

import java.io.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.nio.file.*;

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
        if (WalEntry.getWalEntry(request).getStatus() != RequestStatus.PENDING) {
            remove(WalEntry.getWalEntry(request).getPayload());
        }
                

        if (finishedRequests.add(WalEntry.getWalEntry(request).getWalEntry())) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(WAL_FILE, true))) {
                writer.write(request);
                writer.newLine();                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            
            try {
                File inputFile = new File(WAL_FILE);
                File tempFile = new File("temp_" + WAL_FILE);
                if (!inputFile.exists()) {
                    inputFile.createNewFile();
                }                
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
                    System.err.println("Falha ao renomear o arquivo temporário. Tentando forçar a substituição...");
                    
                    // Tentativa forçada: cópia de conteúdo
                    try (
                        BufferedReader tempReader = new BufferedReader(new FileReader(tempFile));
                        BufferedWriter originalWriter = new BufferedWriter(new FileWriter(inputFile))
                    ) {
                        String line;
                        while ((line = tempReader.readLine()) != null) {
                            originalWriter.write(line);
                            originalWriter.newLine();
                        }
                    } catch (IOException ex) {
                        System.err.println("Falha ao copiar conteúdo manualmente.");
                        ex.printStackTrace();
                    }
                
                    // Limpa temporário
                    tempFile.delete();
                }
                

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static synchronized void save(WalEntry entry) {
        // Se não for mais pendente, remova da cache
        if (entry.getStatus() != RequestStatus.PENDING) {
            remove(entry);
        }
    
        boolean replaced = false;
    
        File inputFile = new File(WAL_FILE);
        File tempFile = new File("temp_" + WAL_FILE);
    
        try (
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
    
                WalEntry existingEntry = WalEntry.getWalEntry(line);
    
                if (existingEntry.getId().equals(entry.getId())) {
                    // Substitui linha
                    writer.write(entry.getWalEntry());
                    replaced = true;
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
    
            // Se não encontrou para substituir, adiciona ao final
            if (!replaced) {
                writer.write(entry.getWalEntry());
                writer.newLine();
            }
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        // Substitui o arquivo original
        try {
            if (!inputFile.exists()) {
                inputFile.createNewFile();
            }      
            if (!inputFile.delete()) {
                throw new IOException("Não foi possível deletar o arquivo original.");
            }
            if (!tempFile.renameTo(inputFile)) {
                System.err.println("Falha ao renomear o arquivo temporário. Tentando forçar a substituição...");
                
                // Tentativa forçada: cópia de conteúdo
                try (
                    BufferedReader tempReader = new BufferedReader(new FileReader(tempFile));
                    BufferedWriter originalWriter = new BufferedWriter(new FileWriter(inputFile))
                ) {
                    String line;
                    while ((line = tempReader.readLine()) != null) {
                        originalWriter.write(line);
                        originalWriter.newLine();
                    }
                } catch (IOException ex) {
                    System.err.println("Falha ao copiar conteúdo manualmente.");
                    ex.printStackTrace();
                }
            
                // Limpa temporário
                tempFile.delete();
            }
                
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        // Atualiza o Set (reconstrói com base no ID)
        finishedRequests.removeIf(s -> WalEntry.getWalEntry(s).getId().equals(entry.getId()));
        finishedRequests.add(entry.getWalEntry());
    }
    
    
}    

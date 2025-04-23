package Classes;

import java.io.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class IdempotencyStore {
    private static final String WAL_FILE = "wal.log";
    private static final Set<String> processedRequests = ConcurrentHashMap.newKeySet();
    

    public static void readDocument(){       
        // Carrega WAL ao iniciar
        try (BufferedReader reader = new BufferedReader(new FileReader(WAL_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                WalEntry entry = WalEntry.getWalEntry(line);
                if (entry.getStatus() == RequestStatus.PENDING || entry.getStatus() == RequestStatus.FAILED) { 
                    processedRequests.add(entry.getPayload());
                }                
            }
        } catch (IOException e) {
            System.out.println("Nenhum WAL existente, iniciando novo.");
        }
    }

    public static boolean isDuplicate(String request) {
        return processedRequests.contains(request);
    }

    public static void save(String request) {
        if (processedRequests.add(WalEntry.getWalEntry(request).getPayload())) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(WAL_FILE, true))) {
                writer.write(request);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }    
}

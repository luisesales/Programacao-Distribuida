package Classes;

import java.io.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class IdempotencyStore {
    private static final String WAL_FILE = "wal.log";
    private static final Set<String> processedRequests = ConcurrentHashMap.newKeySet();
    static{      
        // Carrega WAL ao iniciar
        try (BufferedReader reader = new BufferedReader(new FileReader(WAL_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();      
                if(line.isBlank()) 
                    break;          
                WalEntry entry = WalEntry.getWalEntry(line);                             
                if (entry.getStatus() == RequestStatus.PENDING || entry.getStatus() == RequestStatus.FAILED) { 
                    processedRequests.add(entry.getWalEntry());
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
        else {      
            processedRequests.remove(WalEntry.getWalEntry(request).getPayload());
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
}    

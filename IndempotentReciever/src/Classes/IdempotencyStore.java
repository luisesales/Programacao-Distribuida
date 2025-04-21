package Classes;

import java.io.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class IdempotencyStore {
    private static final String WAL_FILE = "wal.log";
    private static final Set<String> processedRequests = ConcurrentHashMap.newKeySet();

    static {
        // Carrega WAL ao iniciar
        try (BufferedReader reader = new BufferedReader(new FileReader(WAL_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processedRequests.add(line.trim());
            }
        } catch (IOException e) {
            System.out.println("Nenhum WAL existente, iniciando novo.");
        }
    }

    public static boolean isDuplicate(String request) {
        return processedRequests.contains(request);
    }

    public static void save(String request) {
        if (processedRequests.add(request)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(WAL_FILE, true))) {
                writer.write(request);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }    
}

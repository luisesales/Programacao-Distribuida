import java.io.FileWriter;
import java.io.IOException;

public class WalLogger {
    private final String logFilePath;
    private final String logEntry;

    public WalLogger(String logFilePath, String logEntry) {
        this.logFilePath = logFilePath;
        this.logEntry = logEntry;
    }

    public synchronized void writeLog() throws IOException {
        try (FileWriter writer = new FileWriter(logFilePath, true)) {
            writer.write(logEntry + System.lineSeparator());
        }
    }
}
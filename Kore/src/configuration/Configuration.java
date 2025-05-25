package configuration;

import exceptions.ConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private static Properties properties = new Properties();

    static {
        try (InputStream input = Configuration.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new ConfigurationException("application.properties not found");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new ConfigurationException("Read attempt on configuration file " +
                                                     "failed");
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

}

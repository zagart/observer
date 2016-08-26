package by.grodno.zagart.observer.localapp;

import by.grodno.zagart.observer.localapp.interfaces.Loggable;

import java.io.IOException;
import java.util.Properties;

/**
 * Точка входа клиента.
 */
public class ClientMain implements Loggable {

    public static final int ATTEMPTS_LIMIT = 5;

    public static void main(String[] args) {
        boolean success = false;
        int attempts = 0;
        while (!success && (attempts++ < ATTEMPTS_LIMIT)) {
            try {
                new TcpClient("localhost", 8080, getData());
                success = true;
            } catch (IOException ex) {
                success = false;
                logger.error("Error when connecting server -> " + ex.getMessage());
            }
        }
    }

    private static Properties getData() {
        Properties properties = new Properties();
        properties.put("number", "1");
        properties.put("description", "First stand.");
        properties.put("name", "temperature sensor");
        properties.put("status", "33 C");
        return properties;
    }

}

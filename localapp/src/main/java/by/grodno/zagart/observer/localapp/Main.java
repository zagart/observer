package by.grodno.zagart.observer.localapp;

import by.grodno.zagart.observer.localapp.interfaces.Loggable;
import by.grodno.zagart.observer.localapp.network.Serial;
import by.grodno.zagart.observer.localapp.network.TcpClient;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.Properties;
import java.util.TooManyListenersException;

/**
 * Точка входа клиента.
 */
public class Main implements Loggable {

    public static final int ATTEMPTS_LIMIT = 5;

    public static void main(String[] args) {
        try {
//            Serial sender = new Serial("COM1", Serial.PortType.TRANSMITTER);
            Serial receiver = new Serial("COM1", Serial.PortType.RECEIVER);
            System.out.println("Receiver started.");
            receiver.start();
        } catch (NoSuchPortException ex) {
            logger.error("Port not found -> %s" + ex.getMessage());
        } catch (PortInUseException ex1) {
            logger.error(String.format("Port %s already in use -> %s", "{boom}", ex1.getMessage()));
        } catch (IOException ex2) {
            logger.error(String.format("Failed to open input stream -> %s", ex2.getMessage()));
        } catch (UnsupportedCommOperationException ex3) {
            logger.error(String.format("Error when configure port -> %s", ex3.getMessage()));
        } catch (TooManyListenersException ex4) {
            logger.error(String.format("Too many listeners for one port -> %s", ex4.getMessage()));
        }
    }

    private static void connectViaTcp() {
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

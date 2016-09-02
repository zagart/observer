package by.grodno.zagart.observer.localapp.classes;

import by.grodno.zagart.observer.localapp.network.SerialReceiver;
import by.grodno.zagart.observer.localapp.network.TcpClient;
import by.grodno.zagart.observer.localapp.protocols.ObserverSerialProtocol;
import by.grodno.zagart.observer.webapp.entities.Module;
import by.grodno.zagart.observer.webapp.entities.Stand;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TooManyListenersException;

/**
 * Класс-обработчик для TCP-клиента.
 */
public class LocalAppRunner extends Thread {

    public static final String PORT_NAME = "COM1";
    public static final Logger logger = Logger.getLogger(LocalAppRunner.class);
    
    public LocalAppRunner() {
        super("WepAppRunner");
    }

    @Override
    public void run() {
        BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
        boolean running = true;
        try {
            while (running) {
                running = serialReceiverRun();
                if (running) {
                    System.out.println("Failed to start serial receiver. Press enter to continue.");
                    systemIn.readLine();
                }
            }
        }  catch (IOException ex) {
            logger.error(String.format("I/O exception -> ", ex.getMessage()));
        }

    }

    /**
     * Метод пробует создать новый объект класса SerialReceiver. В случае успеха
     * вызывает метод handleSerialData для обработки данных, которые будут периодически
     * обновляться внута объекта класса SerialReceiver.
     *
     * @return false, в случае успешного создания объекта (false в значении, что
     * сервер запущен и пытаться создать его снова больше не нужно), и true, если
     * создания объекта не произошло.
     */
    private synchronized boolean serialReceiverRun() {
        try {
            System.out.println("\nSerial receiver running...");
            SerialReceiver receiver = new SerialReceiver(PORT_NAME, new ObserverSerialProtocol());
            receiver.start();
            handleSerialData(receiver);
            System.out.println("Success.");
            return false;
        } catch (NoSuchPortException ex) {
            logger.warn(String.format("%s: Port not found -> %s",
                    this.getName(),
                    ex.getMessage()));
            return true;
        } catch (PortInUseException ex1) {
            logger.warn(String.format("%s: Port %s already in use -> %s",
                    this.getName(),
                    PORT_NAME,
                    ex1.getMessage()));
            return true;
        } catch (IOException ex2) {
            logger.error(String.format("%s: Failed to open input stream -> %s",
                    this.getName(),
                    ex2.getMessage()));
            return true;
        } catch (UnsupportedCommOperationException ex3) {
            logger.error(String.format("%s: Error when configure port -> %s",
                    this.getName(),
                    ex3.getMessage()));
            return true;
        } catch (TooManyListenersException ex4) {
            logger.error(String.format("%s: Too many listeners for one port -> %s",
                    this.getName(),
                    ex4.getMessage()));
            return true;
        }
    }

    /**
     * Метод создает новый поток для обработки данных, хранящихся в объекте
     * класса SerialReceiver. Если в объекте найдены новые данные, то метод
     * пытается отправить их web-серверу через TCP-соединение.
     *
     * @param receiver Объект класса SerialReceiver.
     */
    private void handleSerialData(SerialReceiver receiver) {
        (new Thread() {
            @Override
            public synchronized void run() {
                try {
                    BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
                    System.out.println("\nPress enter to try to connect server.");
                    systemIn.readLine();
                    TcpClient tcp = new TcpClient("localhost", 8080);
                    System.out.println("Successfully connected to server.");
                    tcp.start();
                    while (true) {
                        String message;
                        if (!(message = receiver.pullMessage()).isEmpty()) {
                            Module module = Module.parseSerialString(message);
                            Stand stand = Stand.parseSerialString(message);
                            ObserverNetworkPackage netPackage = new ObserverNetworkPackage(module, stand);
                            boolean success = false;
                            while (!success) {
                                success = tcp.writeObject(netPackage);
                                this.wait(10);
                            }
                        }
                        this.wait(10);
                    }
                } catch (IOException ex) {
                    logger.warn(String.format("Failed to open TCP-connection -> %s", ex.getMessage()));
                    handleSerialData(receiver);
                } catch (InterruptedException ex1) {
                    logger.error(String.format("Illegal attempt to get monitor -> ", ex1.getMessage()));
                }
            }
        }).start();
    }

}

package by.grodno.zagart.observer.localapp.classes;

import by.grodno.zagart.observer.localapp.network.SerialReceiver;
import by.grodno.zagart.observer.localapp.network.TcpClient;
import by.grodno.zagart.observer.localapp.protocols.ObserverSerialProtocol;
import by.grodno.zagart.observer.webapp.entities.Module;
import by.grodno.zagart.observer.webapp.entities.Stand;
import by.grodno.zagart.observer.localapp.interfaces.Loggable;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.TooManyListenersException;

/**
 * Класс-обработчик для TCP-клиента.
 */
public class TcpClientRunner extends Thread implements Loggable {

    public static final String PORT_NAME = "COM1";

    public TcpClientRunner() {
        super("TcpServerRunner");
    }

    @Override
    public void run() {
        serialReceiverRun();
    }

    private synchronized void serialReceiverRun() {
        try {
            SerialReceiver receiver = new SerialReceiver(PORT_NAME, new ObserverSerialProtocol());
            logger.info("Receiver started.");
            receiver.start();
            handleSerialData(receiver);
        } catch (NoSuchPortException ex) {
            logger.error(String.format("%s: Port not found -> %s",
                    this.getName(),
                    ex.getMessage()));
        } catch (PortInUseException ex1) {
            logger.error(String.format("%s: Port %s already in use -> %s",
                    this.getName(),
                    PORT_NAME,
                    ex1.getMessage()));
        } catch (IOException ex2) {
            logger.error(String.format("%s: Failed to open input stream -> %s",
                    this.getName(),
                    ex2.getMessage()));
        } catch (UnsupportedCommOperationException ex3) {
            logger.error(String.format("%s: Error when configure port -> %s",
                    this.getName(),
                    ex3.getMessage()));
        } catch (TooManyListenersException ex4) {
            logger.error(String.format("%s: Too many listeners for one port -> %s",
                    this.getName(),
                    ex4.getMessage()));
        }
    }

    private void handleSerialData(SerialReceiver receiver) {
        (new Thread() {
            @Override
            public synchronized void run() {
                try {
                    TcpClient tcp = new TcpClient("localhost", 8080);
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
                    logger.error(String.format("%s: Failed to open TCP-connection -> %s",
                            this.getName(),
                            ex.getMessage()));
                } catch (InterruptedException ex1) {
                    logger.error(String.format("%s: Illegal attempt to get monitor -> ",
                            this.getName(),
                            ex1.getMessage()));
                }
            }
        }).start();
    }

}

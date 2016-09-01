package by.grodno.zagart.observer.webapp.classes;

import by.grodno.zagart.observer.localapp.classes.ObserverNetworkPackage;
import by.grodno.zagart.observer.webapp.interfaces.Loggable;
import by.grodno.zagart.observer.webapp.network.TcpListener;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Точка входа сервера.
 */
public class TcpServerRunner extends Thread implements Loggable {

    public TcpServerRunner() {
        super("TcpServerRunner");
    }

    @Override
    public void run() {
        tcpServerRun();
    }

    private synchronized void tcpServerRun() {
        System.out.println("Server started. Waiting for connections...");
        try (ServerSocket socket = new ServerSocket(8080)) {
            TcpListener server;
            while ((server = new TcpListener(socket.accept())) != null) {
                System.out.println("New connection. Total quantity of connections -> " + TcpListener.getClientsQuantity());
                server.start();
                handleTcpData(server);
            }
        } catch (IOException ex) {
            logger.error(String.format("%s Error when establishing connection -> %s",
                    this.getName(),
                    ex.getMessage()));
        }
    }

    private void handleTcpData(TcpListener server) {
        (new Thread() {
            @Override
            public synchronized void run() {
                try {
                    while (true) {
                        Object obj;
                        if ((obj = server.pullObject()) != null) {
                            if (isPackage(obj)) {
                                logger.info(String.format("%s: New package received -> %s",
                                        this.getName(),
                                        obj.toString()));
                            }
                        }
                        this.wait(10);
                    }
                } catch (InterruptedException ex) {
                    logger.error(String.format("%s: Error when thread waiting -> %s",
                            this.getClass().getSimpleName(),
                            ex.getMessage()));
                }
            }
        }).start();
    }

    private boolean isPackage(Object obj) {
        if (obj instanceof ObserverNetworkPackage) {
            return true;
        }
        return false;
    }

}

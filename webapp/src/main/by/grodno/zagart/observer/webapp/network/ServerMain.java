package by.grodno.zagart.observer.webapp.network;

import by.grodno.zagart.observer.webapp.interfaces.Loggable;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Точка входа сервера.
 */
public class ServerMain extends Thread implements Loggable {

    @Override
    public void run() {
        try {
            ServerSocket socket = new ServerSocket(8080);
            TcpListener server;
            while (true) {
                server = new TcpListener(socket.accept());
                server.setPriority(10);
                System.out.println("New client connected. Total quantity of clients -> " + TcpListener.getClientsQuantity());
                server.start();
            }
        } catch (IOException ex) {
            logger.error("Error when establishing connection -> " + ex.getMessage());
        }
    }

}

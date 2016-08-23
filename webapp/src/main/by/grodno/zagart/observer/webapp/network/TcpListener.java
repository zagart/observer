package by.grodno.zagart.observer.webapp.network;

import by.grodno.zagart.observer.webapp.interfaces.Loggable;
import by.grodno.zagart.observer.webapp.utils.DataUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Класс, отвечающий за сетевое соединение на стороне
 * сервера.
 */
public class TcpListener extends Thread implements Loggable {

    private int port = 8080;

    public TcpListener() {
        this.start();
    }

    public TcpListener(int port) {
        this.port = port;
        this.start();
    }

    public int getPort() {
        return port;
    }

    @Override
    public synchronized void run() {
        try (
                ServerSocket serverSocket = new ServerSocket(port);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            out.println("TCP-listener started..");
            String message;
            while ((message = in.readLine()) != null) {
                if (message.equals("connect")) {
                    out.println("Connected.");
                    while ((message = in.readLine()) != null) {
                        if (message.equals("disconnect")) {
                            out.println("Disconnected.");
                            break;
                        }
                        try {
                            DataUtil.saveDataFromTcp(message);
                            out.println("Data received.");
                        } catch (NoClassDefFoundError ex) {
                            out.println("Data format probably wrong.. Failed to retrieve data.");
                            logger.error("Error when retrieving data from TCP: " + ex.toString());
                        }
                    }
                }  else {
                    out.println("Unknown command.");
                }
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage() + ex.getStackTrace());
        }
    }

}

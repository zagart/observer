package by.grodno.zagart.observer.webapp.network;

import by.grodno.zagart.observer.webapp.interfaces.Loggable;

import java.io.*;
import java.net.ServerSocket;
import java.util.Properties;

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
        try (ServerSocket serverSocket = new ServerSocket(port); IOStreamHandler io = new IOStreamHandler(serverSocket)) {
            io.getOutput().println("flag");
            io.getOutput().println("Real message!");
        } catch (IOException ex) {
            ex.printStackTrace();
            logger.error("server: I/O exception!");
        } /** catch (ClassNotFoundException ex1) {
            logger.error("server: UID serial version not correct!");
        } **/
    }

    private Properties getPropertiesFromTcp(ObjectInputStream reader,
                                            PrintWriter writer) throws ClassNotFoundException, IOException {
        Object obj;
        while ((obj = reader.readObject()) != null) {
            if (obj instanceof Properties) {
                Properties properties = (Properties) obj;
                System.out.println("success -> " + properties);
                writer.println("ready");
                return properties;
            } else {
                writer.println("unknown data!");
            }
        }
        return null;
    }

}

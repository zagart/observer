package by.grodno.zagart.observer.webapp.network;

import by.grodno.zagart.observer.webapp.interfaces.Closeable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;

/**
 * Класс, отвечающий за сетевое соединение на стороне
 * сервера. Наследует интерфейс Closeable, который упрощает
 * освобождение ресурсов. Интерфейс Closeable также наследует
 * интерфейс Loggable, что позволяет и данному классу также
 * использовать логгер.
 */
public class TcpListener extends Thread implements Closeable {

    private Socket socket;
    private static int clientsQuantity = 0;

    public TcpListener(Socket socket) {
        super("TcpListener");
        this.socket = socket;
        clientsQuantity++;
    }

    public static int getClientsQuantity() {
        return clientsQuantity;
    }

    @Override
    public synchronized void run() {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            out.println("ready");
            Properties data = getPropertiesFromTcp(in);
            if (data != null) {
                out.println("success -> " + data);
            } else {
                out.println("failed");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            logger.error(String.format("%s: I/O exception -> %s",
                    this.getClass().getSimpleName(),
                    ex.getMessage()));
        } catch (ClassNotFoundException ex1) {
            logger.error(String.format("%s: UID serial version not correct! -> %s",
                    this.getClass().getSimpleName(),
                    ex1.getMessage()));
        } finally {
            closeCloseable(socket);
        }
    }

    @Override
    public void close() {
        closeCloseable(socket);
    }

    private Properties getPropertiesFromTcp(ObjectInputStream in) throws ClassNotFoundException, IOException {
        Object obj = in.readObject();
        if (obj instanceof Properties) {
            Properties properties = (Properties) obj;
            return properties;
        }
        return null;
    }

}

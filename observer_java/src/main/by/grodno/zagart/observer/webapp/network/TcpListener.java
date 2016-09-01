package by.grodno.zagart.observer.webapp.network;

import by.grodno.zagart.observer.webapp.interfaces.Closeable;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

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
    private ObjectInputStream input;
    private PrintWriter output;
    private Queue<Object> storage = new ArrayBlockingQueue<>(Byte.MAX_VALUE);

    public TcpListener(Socket socket) throws IOException {
        super("TcpListener");
        this.socket = socket;
        this.input = new ObjectInputStream(socket.getInputStream());
        this.output = new PrintWriter(socket.getOutputStream(), true);
        clientsQuantity++;
    }

    public static int getClientsQuantity() {
        return clientsQuantity;
    }

    @Override
    public void run() {
        processRun();
    }

    @Override
    public void close() {
        closeCloseable(input);
        closeCloseable(output);
        closeCloseable(socket);
    }

    private synchronized void processRun() {
        try {
            output.println("ready");
            while (true) {
                Object obj;
                if ((obj = readObject()) != null) {
                    storage.offer(obj);
                    output.println("ready");
                }
                this.wait(10);
            }
        } catch (IOException ex) {
            clientsQuantity--;
            logger.error(String.format("%s: I/O exception -> %s",
                    this.getClass().getSimpleName(),
                    ex.getMessage()));
        } catch (ClassNotFoundException ex1) {
            clientsQuantity--;
            logger.error(String.format("%s: UID serial version not correct! -> %s",
                    this.getClass().getSimpleName(),
                    ex1.getMessage()));
        } catch (InterruptedException ex1) {
            clientsQuantity--;
            logger.error(String.format("%s: Attempt to get monitor when thread waiting -> %s",
                    this.getName(),
                    ex1.getMessage()));
        }
    }

    private Object readObject() throws IOException, ClassNotFoundException, InterruptedException {
        try {
            return input.readObject();
        } catch (EOFException ex) {
            logger.error(String.format("%s: Can't read object, no objects to read.",
                    this.getName()));
            this.wait(1000);
            return false;
        }
    }

    public Object pullObject() {
        if (!storage.isEmpty()) {
            return storage.poll();
        }
        return null;
    }

}

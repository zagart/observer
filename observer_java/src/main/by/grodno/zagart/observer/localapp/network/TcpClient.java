package by.grodno.zagart.observer.localapp.network;

import by.grodno.zagart.observer.localapp.interfaces.Closeable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Класс, отвечающий за сетевое соединение(TCP) на стороне
 * клиента. Наследует интерфейс Closeable, который упрощает
 * освобождение ресурсов. Интерфейс Closeable также наследует
 * интерфейс Loggable, что позволяет и данному классу также
 * использовать логгер.
 */
public class TcpClient extends Thread implements Closeable {

    private String hostName;
    private int hostPort;
    private Socket socket;
    private BufferedReader input;
    private ObjectOutputStream output;
    private boolean serverReady = false;

    public TcpClient(String hostName, int hostPort) throws IOException {
        super("TcpClient");
        init(hostName, hostPort);
    }

    private void init(String hostName, int hostPort) throws IOException {
        this.hostName = hostName;
        this.hostPort = hostPort;
        socket = new Socket(hostName, hostPort);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new ObjectOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        processRun();
    }

    private synchronized void processRun() {
        try {
            while (true) {
                if (!serverReady) {
                    String response = input.readLine();
                    if (response.equals("ready")) {
                        printResponse(response);
                        serverReady = true;
                    }
                }
                this.wait(10);
            }
        } catch (IOException ex) {
            logger.error(String.format("%s: Error when reading response -> %s",
                    this.getClass().getSimpleName(),
                    ex.getMessage()));
        } catch (InterruptedException ex1) {
            logger.error(String.format("%s: Error when thread waiting -> %s",
                    this.getClass().getSimpleName(),
                    ex1.getMessage()));
        }
    }

    @Override
    public void close() {
        closeCloseable(input);
        closeCloseable(output);
        closeCloseable(socket);
    }

    public boolean writeObject(Object obj) throws IOException {
        if (serverReady) {
            output.writeObject(obj);
            serverReady = false;
            return true;
        }
        return false;
    }

    private void printResponse(String response) {
        System.out.println(String.format("%s:%d -> %s", hostName, hostPort, response));
    }

}

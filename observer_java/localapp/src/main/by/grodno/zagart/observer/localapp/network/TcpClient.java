package by.grodno.zagart.observer.localapp.network;

import by.grodno.zagart.observer.localapp.interfaces.Closeable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;

/**
 * Класс, отвечающий за сетевое соединение(TCP) на стороне
 * клиента. Наследует интерфейс Closeable, который упрощает
 * освобождение ресурсов. Интерфейс Closeable также наследует
 * интерфейс Loggable, что позволяет и данному классу также
 * использовать логгер.
 */
public class TcpClient extends Thread implements Closeable {

    private String hostName = "localhost";
    private int hostPort = 8080;
    private Socket socket;
    private Properties data;

    public TcpClient(String hostName, int hostPort, Properties data) throws IOException {
        super("TcpClient");
        this.hostName = hostName;
        this.hostPort = hostPort;
        this.data = data;
        socket = new Socket(hostName, hostPort);
        this.start();
    }

    @Override
    public synchronized void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
        ) {
            sendPropertiesViaTcp(in, out, data);
            printResponse(in.readLine());
            this.wait(10);
        } catch (IOException ex) {
            logger.error(String.format("%s: Error when reading response -> %s",
                    this.getClass().getSimpleName(),
                    ex.getMessage()));
        } catch (InterruptedException ex1) {
            logger.error(String.format("%s: Error when thread waiting -> %s",
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

    private boolean sendPropertiesViaTcp(BufferedReader in,
                                         ObjectOutputStream out,
                                         Properties properties) throws IOException {
        String response;
        while ((response = in.readLine()) != null) {
            printResponse(response);
            if (response.equals("ready")) {
                out.writeObject(properties);
                return true;
            }
        }
        return false;
    }

    private void printResponse(String response) {
        System.out.println(String.format("%s:%d -> %s", hostName, hostPort, response));
    }

}

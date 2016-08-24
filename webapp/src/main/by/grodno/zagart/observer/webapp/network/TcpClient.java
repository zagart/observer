package by.grodno.zagart.observer.webapp.network;

import by.grodno.zagart.observer.webapp.interfaces.Loggable;

import java.io.*;
import java.net.Socket;
import java.util.Properties;

/**
 * Класс, отвечающий за сетевое соединение на стороне
 * клиента.
 */
public class TcpClient extends Thread implements Loggable {

    private String hostName = "localhost";
    private int hostPort = 8080;

    public TcpClient() {
        this.start();
    }

    public TcpClient(String hostName, int hostPort) {
        this.hostName = hostName;
        this.hostPort = hostPort;
        this.start();
    }

    public String getHostName() {
        return hostName;
    }

    public int getHostPort() {
        return hostPort;
    }

    @Override
    public synchronized void run() {
        try (Socket clientSocket = new Socket(hostName, hostPort); IOStreamHandler io = new IOStreamHandler(clientSocket)) {
            if (io.getInput().readLine() != null) {
                System.out.println("Catch! -> " + io.getInput().readLine());
            }
        } catch (IOException ex) {
            logger.error("client: I/O exception!");
        }
    }

    private boolean sendProperties(BufferedReader reader,
                                   ObjectOutputStream writer,
                                   PrintWriter out,
                                   Properties properties,
                                   String command) throws IOException {
        out.println("send data");
        String response;
        while ((response = reader.readLine()) != null) {
            System.out.println("server: " + response);
            if (response.equals("ready") && command.equals("go")) {
                writer.writeObject(properties);
                return true;
            }
        }
        System.out.println("not connected.");
        return false;
    }

    private Properties getData() {
        Properties properties = new Properties();
        properties.put("number", "1");
        properties.put("description", "First stand.");
        properties.put("name", "temperature sensor");
        properties.put("status", "33 C");
        return properties;
    }

}

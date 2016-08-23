package by.grodno.zagart.observer.webapp.network;

import by.grodno.zagart.observer.webapp.interfaces.Loggable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
        try (
                Socket clientSocket = new Socket(hostName, hostPort);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("TcpClient running...");
            String inputMsg;
            while ((inputMsg = in.readLine()) != null) {
                System.out.println("TcpListener: " + inputMsg);
                System.out.print("Client: ");
                String message = sysIn.readLine();
                out.println(message);
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage() + ex.getStackTrace());
        }
    }

}

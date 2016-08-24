package by.grodno.zagart.observer.webapp;

import by.grodno.zagart.observer.webapp.network.TcpClient;
import by.grodno.zagart.observer.webapp.network.TcpListener;

public class Main {

    public static void main(String...args) {
        TcpListener tcpListener = new TcpListener();
        TcpClient tcpClient = new TcpClient();
    }

}
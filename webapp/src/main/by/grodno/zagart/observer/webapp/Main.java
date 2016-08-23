package by.grodno.zagart.observer.webapp;

import by.grodno.zagart.observer.webapp.network.TcpClient;
import by.grodno.zagart.observer.webapp.network.TcpListener;

import java.util.Properties;

public class Main {

    public static void main(String...args) {
        Properties properties = new Properties();
        properties.put("number", "1");
        properties.put("description", "First stand.");
        properties.put("name", "temperature sensor");
        properties.put("status", "33 C");
        System.out.println(properties.toString());
        TcpListener tcpListener = new TcpListener();
        TcpClient tcpClient = new TcpClient();
    }

}
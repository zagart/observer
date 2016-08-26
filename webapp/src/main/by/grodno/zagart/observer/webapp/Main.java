package by.grodno.zagart.observer.webapp;

import by.grodno.zagart.observer.webapp.interfaces.Loggable;
import by.grodno.zagart.observer.webapp.network.ClientMain;
import by.grodno.zagart.observer.webapp.network.ServerMain;
import by.grodno.zagart.observer.webapp.network.TcpClient;
import by.grodno.zagart.observer.webapp.network.TcpListener;

import java.io.IOException;

public class Main implements Loggable {

    public static void main(String...args) {
        new ServerMain().start();
        new ClientMain().start();
    }

}
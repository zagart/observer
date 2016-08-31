package by.grodno.zagart.observer.webapp;

import by.grodno.zagart.observer.webapp.network.TcpServerRunner;

public class Main {

    public static void main(String...args) {
        new TcpServerRunner().start();
    }

}
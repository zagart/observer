package by.grodno.zagart.observer.webapp;

import by.grodno.zagart.observer.webapp.interfaces.Loggable;
import by.grodno.zagart.observer.webapp.network.ServerMain;

public class Main implements Loggable {

    public static void main(String...args) {
        new ServerMain().start();
    }

}
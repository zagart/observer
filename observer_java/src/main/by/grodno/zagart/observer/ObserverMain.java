package by.grodno.zagart.observer;

import by.grodno.zagart.observer.localapp.classes.TcpClientRunner;
import by.grodno.zagart.observer.webapp.classes.TcpServerRunner;

/**
 * Точка входа проекта.
 */
public class ObserverMain {

    public static void main(String[] args) {
        new TcpServerRunner().start();
        new TcpClientRunner().start();
    }

}

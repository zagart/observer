package by.grodno.zagart.observer.localapp;

import by.grodno.zagart.observer.localapp.classes.TcpClientRunner;

/**
 * Точка входа локального приложения.
 */
public class LocalAppMain {

    public static void main(String[] args) {
        new TcpClientRunner().start();
    }

}

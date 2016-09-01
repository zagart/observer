package by.grodno.zagart.observer.webapp;

import by.grodno.zagart.observer.webapp.classes.TcpServerRunner;

/**
 * Точка входа веб-приложения.
 */
public class WebAppMain {

    public static void main(String[] args) {
        new TcpServerRunner().start();
    }

}

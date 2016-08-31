package by.grodno.zagart.observer.localapp;

import by.grodno.zagart.observer.localapp.network.TcpClientRunner;

/**
 * Точка входа клиента.
 */
public class Main {

    public static void main(String[] args) {
        (new TcpClientRunner()).start();
    }

}

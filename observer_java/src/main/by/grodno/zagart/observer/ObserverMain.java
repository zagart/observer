package by.grodno.zagart.observer;

import by.grodno.zagart.observer.localapp.classes.LocalAppRunner;
import by.grodno.zagart.observer.webapp.classes.WepAppRunner;

public class ObserverMain {

    /**
     * Точка входа проекта.
     */
    public static void main(String[] args) {
        new WepAppRunner().start();
        new LocalAppRunner().start();
    }

}

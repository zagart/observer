package by.grodno.zagart.observer.webapp.interfaces;

import org.apache.log4j.Logger;

/**
 * Реализация интерфейса позволяет классу использовать
 * логгер log4j.
 */
public interface Loggable {

    Logger logger = Logger.getLogger(Logger.class);

}

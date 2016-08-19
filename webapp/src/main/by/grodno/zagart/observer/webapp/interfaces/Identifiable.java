package by.grodno.zagart.observer.webapp.interfaces;

import java.io.Serializable;

/**
 * Реализвация этого интерфейса классом указывает на то, что
 * этот класс имеет уникальный идентификатор. Позволяет сделать
 * логирование более информативным.
 */
public interface Identifiable<PK extends Serializable> {

    PK getId();

}

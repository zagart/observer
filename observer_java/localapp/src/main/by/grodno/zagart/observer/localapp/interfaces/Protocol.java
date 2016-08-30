package by.grodno.zagart.observer.localapp.interfaces;

import java.io.IOException;
import java.util.List;

/**
 * Имплементации этого интерфейса имеют метод для обработки данных из потока
 * согласно определенным правилам. Данные должны быть представлены в виде
 * коллекции List<Integer>.
 */
public interface Protocol {

    int getDataSize();

    boolean process(List<Integer> data) throws IOException;

}

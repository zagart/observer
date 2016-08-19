package by.grodno.zagart.observer.webapp.interfaces;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

/**
 * Реализация этого интерфейса классом позволяет ему
 * использовать рефлексию.
 */
public interface Reflective extends Loggable {

    /**
     * Метод создает объект класса, который является параметром в
     * generic-параметрах класса-имплементатора на указанной позиции.
     *
     * @param parameterPosition
     * @return
     */
    default Object getGenericObject(int parameterPosition) {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        Class<?> clazz = (Class<?>) parameterizedType.getActualTypeArguments()[parameterPosition];
        Constructor<?> constructor = clazz.getConstructors()[0];
        Object object = null;
        try {
            object = constructor.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return object;
    }

}

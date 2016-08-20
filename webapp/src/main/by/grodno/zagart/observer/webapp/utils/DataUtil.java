package by.grodno.zagart.observer.webapp.utils;

import by.grodno.zagart.observer.webapp.entities.Module;
import by.grodno.zagart.observer.webapp.entities.Stand;
import org.apache.commons.lang.RandomStringUtils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Класс с методами для более удобного управления
 * данными.
 */
public class DataUtil {

    public static Module getNewModule() {
        Module module = new Module();
        module.setName(RandomStringUtils.randomAlphabetic(10));
        module.setStatusInfo(RandomStringUtils.randomAlphabetic(20));
        module.setStatusChangeDate(new Date());
        return module;
    }

    public static Stand getNewStand() {
        Stand stand = new Stand();
        stand.setNumber("1");
        stand.setDescription(RandomStringUtils.randomAlphabetic(20));
        stand.setModuleList(new ArrayList<>());
        return stand;
    }

}

package by.grodno.zagart.observer.webapp.utils;

import by.grodno.zagart.observer.webapp.entities.Module;
import by.grodno.zagart.observer.webapp.entities.Stand;
import by.grodno.zagart.observer.webapp.services.impl.ModuleServiceImpl;
import by.grodno.zagart.observer.webapp.services.impl.StandServiceImpl;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

/**
 * Класс с методами для более эффективного манипулирования
 * данными.
 */
public class DataUtil {

    public static final Logger logger = Logger.getLogger(DataUtil.class);

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

    public static void saveDataFromTcp(String data) {
        ModuleServiceImpl moduleService = new ModuleServiceImpl();
        StandServiceImpl standService = new StandServiceImpl();
        Module module = Module.parseTcpString(data);
        Stand stand = Stand.parseTcpString(data);
        moduleService.save(module);
        standService.save(stand);
        stand.addModule(module);
        moduleService.update(module);
        standService.update(stand);
    }

    public static Properties convertStringToProperties(final String strProperties) throws IOException {
        Properties properties = new Properties();
        String key;
        String value;
        int firstSeparator = -1;
        int secondSeparator;
        int endSeparator = strProperties.lastIndexOf("}");
        try {
            while (true) {
                secondSeparator = strProperties.indexOf('=', firstSeparator);
                key = strProperties.substring(2 + firstSeparator, secondSeparator);
                firstSeparator = secondSeparator;
                secondSeparator = strProperties.indexOf(',', firstSeparator);
                if (secondSeparator < 0) {
                    value = strProperties.substring(1 + firstSeparator, endSeparator);
                    properties.put(key, value);
                    break;
                }
                value = strProperties.substring(1 + firstSeparator, secondSeparator);
                firstSeparator = secondSeparator;
                properties.put(key, value);
            }
        } catch (StringIndexOutOfBoundsException ex) {
            logger.error(String.format("%s: Wrong data format for converting! -> %s",
                    "convertStringToProperties",
                    ex.getMessage()));
            throw new NoClassDefFoundError();
        }
        return properties;
    }

}

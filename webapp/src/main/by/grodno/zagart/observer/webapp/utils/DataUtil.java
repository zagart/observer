package by.grodno.zagart.observer.webapp.utils;

import by.grodno.zagart.observer.webapp.entities.Module;
import by.grodno.zagart.observer.webapp.entities.Stand;
import by.grodno.zagart.observer.webapp.interfaces.Loggable;
import by.grodno.zagart.observer.webapp.services.impl.ModuleServiceImpl;
import by.grodno.zagart.observer.webapp.services.impl.StandServiceImpl;
import org.apache.commons.lang.RandomStringUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

/**
 * Класс с методами для более эффективного манипулирования
 * данными.
 */
public class DataUtil implements Loggable {

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
        Module module = Module.setModuleUsingTcpData(data);
        Stand stand = Stand.setStandUsingTcpData(data);
        moduleService.save(module);
        standService.save(stand);
        stand.addModule(module);
        moduleService.update(module);
        standService.update(stand);
    }

    public static Properties convertTcpDataToProperties(final String tcpData) throws IOException {
        Properties properties = new Properties();
        String key;
        String value;
        int firstSeparator = -1;
        int secondSeparator;
        int endSeparator = tcpData.lastIndexOf("}");
        try {
            while (true) {
                secondSeparator = tcpData.indexOf('=', firstSeparator);
                key = tcpData.substring(2 + firstSeparator, secondSeparator);
                firstSeparator = secondSeparator;
                secondSeparator = tcpData.indexOf(',', firstSeparator);
                if (secondSeparator < 0) {
                    value = tcpData.substring(1 + firstSeparator, endSeparator);
                    properties.put(key, value);
                    break;
                }
                value = tcpData.substring(1 + firstSeparator, secondSeparator);
                firstSeparator = secondSeparator;
                properties.put(key, value);
            }
        } catch (StringIndexOutOfBoundsException ex) {
            logger.error("convertTcpDataToProperties(final String tcpData){...}: " +
                    "Wrong data format for converting!");
            throw new NoClassDefFoundError();
        }
        return properties;
    }

}

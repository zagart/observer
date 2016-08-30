package by.grodno.zagart.observer.localapp.network;

import by.grodno.zagart.observer.localapp.interfaces.Loggable;
import by.grodno.zagart.observer.localapp.interfaces.Protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс описывает правила обмена данными через последовательный порт
 * с микроконтроллером и выполняет их обработку в соответствии с этими
 * правилами.
 */
public class ObserverSerialProtocol implements Protocol, Loggable {

    private static final int dataLength = 6;

    @Override
    public int getDataSize() {
        return dataLength;
    }

    @Override
    public void process(List<Integer> data) {
        if (!data.isEmpty()) {
            logger.info(String.format("New data -> %s \n", convertList(data)));
        }
    }

    public String getConstantByValue(int value) {
        for (Constant c : Constant.values()) {
            if (c.value == value) {
                return c.name();
            }
        }
        return "";
    }

    public List<String> convertList(List<Integer> data) {
        List<String> convertedData = new ArrayList<>();
        for (Integer dataByte : data) {
            String convertedByte = getConstantByValue(dataByte);
            if (!convertedByte.isEmpty()) {
                convertedData.add(convertedByte);
            } else {
                convertedData.add(dataByte.toString());
            }
        }
        return convertedData;
    }

    private enum Constant {
        INIT(2),
        TEMP_CHANGE (3),
        LIGHT_CHANGE (4),
        LCD_NEW_OUTPUT (5),
        STAND_MC (10),
        LIGHT_SENSOR (11),
        TEMP_SENSOR (12),
        LCD_DISPLAY (13),
        NULL (200),
        OERR (201),
        FERR (202),
        NO_MSG (203),
        SYSTEM_EXIT (204),
        MSG_START (205),
        MSG_END (206);

        private final int value;

        Constant(int value) {
            this.value = value;
        }
    }

}

package by.grodno.zagart.observer.localapp.network;

import by.grodno.zagart.observer.localapp.interfaces.Loggable;
import by.grodno.zagart.observer.localapp.interfaces.Protocol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static by.grodno.zagart.observer.localapp.network.ObserverSerialProtocol.Constant.*;

/**
 * Класс содержит набор констант и методы для обработки данных,
 * соответствующий правилам обмена данными через последовательный
 * порт единицами проекта Observer.
 */
public class ObserverSerialProtocol implements Protocol, Loggable {

    private static final int dataLength = 6;
    private final MessageFormatChecker checker = new MessageFormatChecker();

    @Override
    public int getDataSize() {
        return dataLength;
    }

    @Override
    public boolean process(List<Integer> data) throws IOException {
        if (!data.isEmpty() ) {
            if (data.size() == dataLength) {
                if (checker.isMessage(data)) {
                    if (checker.isValidArguments(data)) {
                        logger.info(String.format("New data -> %s", convertList(data)));
                        return true;
                    } else {
                        throw new IOException("Incorrect MODULE(3)/STATUS(4) arguments.");
                    }
                } else {
                    throw new IOException("START/END message marks is missing.");
                }
            } else {
                throw new IOException("Wrong message length.");
            }
        }
        return false;
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

    public enum Constant {
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

    private class MessageFormatChecker {
        private boolean isMessage(List<Integer> data) throws IOException {
            int firstElement = 0;
            int lastElement = dataLength - 1;
            if (data.size() == dataLength) {
                if (data.get(firstElement) == MSG_START.value && data.get(lastElement) == MSG_END.value) {
                    return true;
                }
            }
            return false;
        }

        private boolean isValidArguments(List<Integer> data) throws IOException {
            int module = 2;
            int status = 3;
            if (data.size() == dataLength) {
                if (isConstant(data.get(module)) && isConstant(data.get(status))) {
                    return true;
                }
            }
            return false;
        }

        private boolean isConstant(int value) {
            for (Constant c : Constant.values()) {
                if (c.value == value) {
                    return true;
                }
            }
            return false;
        }
    }

}

package by.grodno.zagart.observer.localapp.network;

import by.grodno.zagart.observer.localapp.interfaces.Loggable;
import by.grodno.zagart.observer.localapp.interfaces.SerialProtocol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static by.grodno.zagart.observer.localapp.network.ObserverSerialProtocol.Constant.MSG_HEAD;
import static by.grodno.zagart.observer.localapp.network.ObserverSerialProtocol.Constant.MSG_TAIL;

/**
 * Класс содержит набор констант и методы для обработки данных,
 * соответствующий правилам обмена данными через последовательный
 * порт единицами проекта Observer.
 */
public class ObserverSerialProtocol implements SerialProtocol, Loggable {

    private static final int messageLength = 6;
    private static final int speed = 9600;

    private final MessageFormatChecker checker = new MessageFormatChecker();

    @Override
    public int getMessageLength() {
        return messageLength;
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    @Override
    public String process(List<Integer> serialData) throws IOException {
        if (!serialData.isEmpty() ) {
            if (serialData.size() == messageLength) {
                if (checker.isMessage(serialData)) {
                    if (checker.isValidArguments(serialData)) {
                        String stringData = dataToString(serialData);
                        if (!stringData.contains("LCD")) {
                            logger.info(String.format("New data -> %s", dataToString(serialData)));
                            return compilePropertiesString(serialData);
                        }
                    } else {
                        throw new IOException("Incorrect MODULE(3)/STATUS(4) arguments.");
                    }
                } else {
                    throw new IOException("HEAD/TAIL message marks are missing.");
                }
            } else {
                throw new IOException("Wrong message length.");
            }
        }
        return "";
    }

    private String compilePropertiesString(List<Integer> serialData) {
        int standNumber = 1;
        int moduleName = 2;
        int eventDescription = 3;
        int eventValue = 4;
        Properties properties = new Properties();
        properties.put("stand", serialData.get(standNumber));
        properties.put("module", getConstantDescriptionByValue(serialData.get(moduleName)));
        properties.put("event", getConstantDescriptionByValue(serialData.get(eventDescription)));
        properties.put("value", serialData.get(eventValue));
        return properties.toString();
    }

    private String dataToString(List<Integer> serialData) {
        List<String> convertedData = new ArrayList<>();
        for (Integer dataByte : serialData) {
            String convertedByte = getConstantNameByValue(dataByte);
            if (!convertedByte.isEmpty()) {
                convertedData.add(convertedByte);
            } else {
                convertedData.add(dataByte.toString());
            }
        }
        return convertedData.toString();
    }

    private String getConstantDescriptionByValue(int value) {
        for (Constant c : Constant.values()) {
            if (c.value == value) {
                return c.description;
            }
        }
        return "";
    }

    private String getConstantNameByValue(int value) {
        for (Constant c : Constant.values()) {
            if (c.value == value) {
                return c.name();
            }
        }
        return "";
    }

    public enum Constant {
        INIT(2, "Инициализация."),
        TEMP_CHANGE (3, "Изменение температуры."),
        LIGHT_CHANGE (4, "Изменение освещенности."),
        LCD_NEW_OUTPUT (5, "Изменение данных на LCD-дисплее."),
        STAND_MC (10, "Микро-контроллер."),
        LIGHT_SENSOR (11, "Датчик освещенности."),
        TEMP_SENSOR (12, "Датчик температуры."),
        LCD_DISPLAY (13, "LCD-дисплей."),
        NULL (200, "Данные отсутствуют или не предусмотрены для этого события/модуля."),
        OERR (201, "Критическая ошибка модуля USART."),
        FERR (202, "Ошибка стопового бита модуля USART."),
        NO_MSG (203, "Нет ответа."),
        SYSTEM_EXIT (204, "Аварийное отключение/прерывание/перезагрузка."),
        MSG_HEAD(205, "Метка начала сообщения."),
        MSG_TAIL(206, "Метка конца сообщения.");

        private final int value;
        private final String description;

        Constant(int value, String description) {
            this.value = value;
            this.description = description;
        }

    }

    private class MessageFormatChecker {
        private boolean isMessage(List<Integer> data) throws IOException {
            int firstElement = 0;
            int lastElement = messageLength - 1;
            if (data.get(firstElement) == MSG_HEAD.value && data.get(lastElement) == MSG_TAIL.value) {
                    return true;
            }
            return false;
        }

        private boolean isValidArguments(List<Integer> data) throws IOException {
            int module = 2;
            int status = 3;
            if (isConstant(data.get(module)) && isConstant(data.get(status))) {
                    return true;
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

package by.grodno.zagart.observer.localapp.network;


import by.grodno.zagart.observer.localapp.interfaces.Loggable;
import by.grodno.zagart.observer.localapp.interfaces.SerialProtocol;
import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Класс предназначен для обработки данных, поступающих
 * на COM-порт.
 *
 * !!! gnu.io -> сторонняя библиотека для работы с последовательным
 * и параллельным портами данных, требует ручной установки и
 * добавить Maven зависимость в pom.xml недостаточно.
 * Oracle имеет свою имплемантацию этой библиотеки, однако она
 * не поддерживает ОС Windows.
 */
public class SerialReceiver extends Thread implements Loggable {

    private SerialPort port;
    private CommPortIdentifier identifier;
    private final InputStream input; //Используем не буферизированный поток, так как только он корректно читает байт с
                               // последовательного порта (значение от 0 до 255). Вероятно, это связано с
                               // тем, что единственый байтовый тип Java - byte - знаковый и имеет границы [-128; 127].
    private final SerialProtocol protocol;
    private final int bufferSize;
    private final int speed;
    private Queue<String> inbox = new ArrayBlockingQueue<>(Byte.MAX_VALUE);

    public SerialReceiver(String portName, SerialProtocol protocol) throws NoSuchPortException,
            PortInUseException,
            IOException,
            UnsupportedCommOperationException,
            TooManyListenersException {
        super("SerialReceiver");
        findPort(portName);
        portInit();
        this.input = this.port.getInputStream();
        this.protocol = protocol;
        this.bufferSize = protocol.getMessageLength();
        this.speed = protocol.getSpeed();
    }

    @Override
    public void run() {
        waitData();
    }

    private synchronized void waitData() {
        try {
            List<Integer> data;
            while (input != null) {
                data = readBytes();
                String result = protocol.process(data);
                if (!result.isEmpty()) {
                    inbox.offer(result);
                }
                this.wait(10);
            }
            this.port.close();
        } catch (IOException ex) {
            if (!ex.getMessage().contains("No error")) {
                logger.error(String.format("%s: Error when reading stream -> %s",
                        this.getName(),
                        ex.getMessage()));
            }
        } catch (InterruptedException ex1) {
            logger.error(String.format("%s: Attempt to get monitor when thread waiting -> %s",
                    this.getName(),
                    ex1.getMessage()));
        }
    }

    private List<Integer> readBytes() throws IOException {
        List<Integer> bytes = new ArrayList<>();
        int value;
        int counter = 0;
        if (input.available() >= bufferSize) {
            while (counter++ < bufferSize && ((value = readByte()) != -1)) {
                bytes.add(value);
            }
        }
        return bytes;
    }

    private int readByte() throws IOException {
        if (input != null) {
            try {
                return input.read();
            } catch (IOException ex) {
                if (ex.getMessage().contains("Stream closed")) {
                    throw ex;
                }
                return -1;
            }
        }
        return -1;
    }

    private void portInit() throws UnsupportedCommOperationException {
        port.setSerialPortParams(speed,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
        port.setDTR(false);
        port.setRTS(true);
    }

    private void findPort(String portName) throws NoSuchPortException, PortInUseException {
        Enumeration ports = CommPortIdentifier.getPortIdentifiers();
        while (ports.hasMoreElements()) {
            CommPortIdentifier identifier = (CommPortIdentifier)ports.nextElement();
            if (identifier.getName().equals(portName)) {
                this.identifier = identifier;
                this.port = (SerialPort) this.identifier.open("SerialReceiver", 2000);
            }
        }
        if (identifier == null) {
            throw new NoSuchPortException();
        }
    }

    public String pullMessage() {
        if (!inbox.isEmpty()) {
            return inbox.poll();
        }
        return "";
    }

}

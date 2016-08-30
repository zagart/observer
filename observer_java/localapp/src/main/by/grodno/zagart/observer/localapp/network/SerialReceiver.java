package by.grodno.zagart.observer.localapp.network;


import by.grodno.zagart.observer.localapp.interfaces.Loggable;
import by.grodno.zagart.observer.localapp.interfaces.Protocol;
import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;

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
    private final Protocol protocol;
    private final int bufferSize;



    public SerialReceiver(String portName, Protocol protocol) throws NoSuchPortException,
            PortInUseException,
            IOException,
            UnsupportedCommOperationException,
            TooManyListenersException {
        super("SerialReceiver");
        findPort(portName);
        portInit();
        this.input = this.port.getInputStream();
        this.protocol = protocol;
        this.bufferSize = protocol.getDataSize();
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

    private void portInit() throws UnsupportedCommOperationException {
        port.setSerialPortParams(9600,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
        port.setDTR(false);
        port.setRTS(true);
    }

    @Override
    public synchronized void run() {
        waitData();
    }

    private void waitData() {
        try {
            List<Integer> data;
            while (input != null) {
                data = readBytes(bufferSize);
                protocol.process(data);
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

    public List<Integer> readBytes(int size) throws IOException {
        List<Integer> bytes = new ArrayList<>();
        int value;
        int counter = 0;
        while (counter++ < size && ((value = readByte()) != -1)) {
            bytes.add(value);
        }
        return bytes;
    }

    private int readByte() throws IOException {
        if (input != null && input.available() > 0) {
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



}

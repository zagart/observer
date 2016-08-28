package by.grodno.zagart.observer.localapp.network;


import by.grodno.zagart.observer.localapp.interfaces.Loggable;
import gnu.io.*;

import java.io.*;
import java.util.Enumeration;
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
public class Serial extends Thread implements Loggable {

    public enum PortType {
        RECEIVER, TRANSMITTER
    }

    private SerialPort port;
    private CommPortIdentifier identifier;
    private BufferedReader input;
    private Writer output;


    public Serial(String portName, PortType type) throws NoSuchPortException,
            PortInUseException,
            IOException,
            UnsupportedCommOperationException,
            TooManyListenersException {
        super("Serial");
        findPort(portName);
        portInit();
        openStream(type);
    }

    @Override
    public synchronized void run() {
        try {
            String data;
            while (input != null) {
                data = readData();
                if (data != null) {
                    System.out.printf("%s: new symbol -> %s \n", this.identifier.getName(), data);
                }
                this.wait(1000);
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

    @Override
    public String toString() {
        return String.format("{%s, %s}", this.getName(), identifier.getName());
    }

    private void openStream(PortType type) throws IOException {
        if (type == PortType.RECEIVER) {
            this.input = new BufferedReader(new InputStreamReader(this.port.getInputStream()));
        }
        if (type == PortType.TRANSMITTER) {
            this.output = new OutputStreamWriter(this.port.getOutputStream());
        }
    }

    private void findPort(String portName) throws NoSuchPortException, PortInUseException {
        Enumeration ports = CommPortIdentifier.getPortIdentifiers();
        while (ports.hasMoreElements()) {
            CommPortIdentifier identifier = (CommPortIdentifier)ports.nextElement();
            if (identifier.getName().equals(portName)) {
                this.identifier = identifier;
                this.port = (SerialPort) this.identifier.open("Serial", 2000);
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

    private String readData() throws IOException {
        if (input != null && input.ready()) {
            try {
                return String.valueOf(input.read());
            } catch (IOException ex) {
                if (ex.getMessage().contains("Stream closed")) {
                    throw ex;
                }
                return null;
            }
        }
        return null;
    }

    private void sendData() throws IOException, InterruptedException {
        if (output != null) {
            output.write("Hello!");
            output.flush();
            this.wait(10);
        }
    }

}

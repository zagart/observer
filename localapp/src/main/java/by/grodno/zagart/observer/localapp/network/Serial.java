package by.grodno.zagart.observer.localapp.network;


import by.grodno.zagart.observer.localapp.interfaces.Closeable;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.io.*;
import java.util.Enumeration;

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
public class Serial extends Thread implements Closeable {

    public enum PortType {
        RECEIVER, SENDER
    }

    private SerialPort port;
    private CommPortIdentifier identifier;
    private BufferedReader input;
    private PrintWriter output;


    public Serial(String portName, PortType type) throws NoSuchPortException, PortInUseException, IOException {
        super("Serial");
        findPort(portName);
        openStream(type);
    }

    @Override
    public synchronized void run() {
        try {
            if (input != null) {
                System.out.println(input.readLine());
            }
            if (output != null) {
                output.println("Hello!");
            }
        } catch (IOException ex) {
            logger.error(String.format("%s: Error when reading stream -> %s",
                    this.getName(),
                    ex.getMessage()));
        }
    }

    @Override
    public void close() {
        closeCloseable(input);
        if (port != null) {
            port.close();
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
        if (type == PortType.SENDER) {
            this.output = new PrintWriter(this.port.getOutputStream(), true);
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

}

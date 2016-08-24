package by.grodno.zagart.observer.webapp.network;

import by.grodno.zagart.observer.webapp.interfaces.Loggable;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Класс, ответственный за создание потоков ввода/вывода,
 * обслуживающих TCP-соединение.
 */
public class IOStreamHandler implements Loggable, Closeable {

    public enum SocketType {
        SERVER,
        CLIENT
    }

    private Socket socket;
    private ServerSocket serverSocket;
    private SocketType type;
    private BufferedReader objectReader;
    private ObjectOutputStream objectWriter;
    private PrintWriter output;
    private BufferedReader input;
    private BufferedReader console;

    public IOStreamHandler(Socket socket) throws IOException {
        this.socket = socket;
        type = SocketType.CLIENT;
        setUp();
    }

    public IOStreamHandler(ServerSocket serverSocket) throws IOException {
        this.serverSocket = serverSocket;
        type = SocketType.SERVER;
        setUp();
    }

    public void setUp() throws IOException {
        if (type == SocketType.CLIENT) {
            createStreams(socket);
        }
        if (type == SocketType.SERVER) {
            socket = serverSocket.accept();
            createStreams(socket);
        }
    }

    public SocketType getType() {
        return type;
    }

    public BufferedReader getObjectReader() {
        return objectReader;
    }

    public ObjectOutputStream getObjectWriter() {
        return objectWriter;
    }

    public PrintWriter getOutput() {
        return output;
    }

    public BufferedReader getInput() {
        return input;
    }

    public BufferedReader getConsole() {
        return console;
    }

    @Override
    public void close() throws IOException {
        closeCloseable(objectReader);
        closeCloseable(objectWriter);
        closeCloseable(output);
        closeCloseable(input);
        closeCloseable(console);
    }

    private void closeCloseable(Closeable closeable) throws IOException {
        if (closeable != null) {
            closeable.close();
        }
    }

    private void createStreams(Socket socket) {
        try {
            objectReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            objectWriter = new ObjectOutputStream(socket.getOutputStream());
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            console = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException e) {
            logger.error("I/O exception input IOStreamHandler! Failed to set up server stream(s).");
        }
    }

}

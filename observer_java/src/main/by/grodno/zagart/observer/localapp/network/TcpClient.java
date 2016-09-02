package by.grodno.zagart.observer.localapp.network;

import by.grodno.zagart.observer.webapp.interfaces.Closeable;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Класс, отвечающий за сетевое соединение(TCP) на стороне
 * клиента. Наследует интерфейс Closeable, который упрощает
 * освобождение ресурсов.
 */
public class TcpClient extends Thread implements Closeable {

    public static final Logger logger = Logger.getLogger(TcpClient.class);

    private String hostName;
    private int hostPort;
    private Socket socket;
    private BufferedReader input;
    private ObjectOutputStream output;
    private boolean serverReady = false;

    /**
     * Для создания объекта класса необходимы имя хоста и номер порта хоста.
     *
     * @param hostName Имя хоста.
     * @param hostPort Имя порта хоста.
     *
     * @throws IOException
     */
    public TcpClient(String hostName, int hostPort) throws IOException {
        super("TcpClient");
        init(hostName, hostPort);
    }

    /**
     * Метод конфигурирует объект класса и создает сокет на основе
     * переданных данных.
     *
     * @param hostName Имя хоста.
     * @param hostPort Номер порта хоста.
     * @throws IOException
     */
    private void init(String hostName, int hostPort) throws IOException {
        this.hostName = hostName;
        this.hostPort = hostPort;
        socket = new Socket(hostName, hostPort);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new ObjectOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        waitResponse();
    }

    /**
     * Метод ждет ответа от сервера, и, если сервер готов к приему данных,
     * то устанавливает переменную serverReady в true.
     */
    private synchronized void waitResponse() {
        try {
            while (input != null) {
                if (!serverReady) {
                    String response = input.readLine();
                    if (response.equals("ready")) {
                        printResponse(response);
                        serverReady = true;
                    }
                }
                this.wait(10);
            }
        } catch (IOException ex) {
            logger.error(String.format("%s: Error when reading response -> %s",
                    this.getClass().getSimpleName(),
                    ex.getMessage()));
        } catch (InterruptedException ex1) {
            logger.error(String.format("%s: Error when thread waiting -> %s",
                    this.getClass().getSimpleName(),
                    ex1.getMessage()));
        }
    }

    /**
     * Метод освобождает ресурсы класса и закрывает сокет. После вызова этого
     * метода класс перестает выполнять свои функции.
     */
    @Override
    public void close() {
        closeCloseable(input);
        closeCloseable(output);
        closeCloseable(socket);
    }

    /**
     * Если сервер готов к приему данных, то метод записывает в выходной
     * поток, сериализованный объект.
     *
     * @param obj Сериализованный объект.
     * @return
     * @throws IOException
     */
    public boolean writeObject(Object obj) throws IOException {
        if (serverReady) {
            output.writeObject(obj);
            serverReady = false;
            return true;
        }
        return false;
    }

    /**
     * Метод выводит информацию о полученном от сервера ответе
     * в определенном формате.
     *
     * @param response
     */
    private void printResponse(String response) {
        System.out.println(String.format("\n%s:%d -> %s", hostName, hostPort, response));
    }

}

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread implements IServer {
    private int port;
    private boolean running;
    private static IServer iserver;
    private IServerThread serverThread;
    private ISquareController squareController;
    private ILogIt logger;

    public static IServer create(int port, ISquareController controller, ILogIt logger) {
        if (iserver == null) {
            iserver = new Server(port, controller, logger);
        }
        return iserver;
    }

    public int getPort() {
        return port;
    }

    public Server(int port, ISquareController controller, ILogIt logger) {
        running = false;
        this.port = port;
        this.squareController = controller;
        this.logger = logger;
    }

    public void teardown() {
        logger.logInfo("Ending Server");
        running = false;

        serverThread = null;
    }

    @Override
    public void run() {
        running = true;
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            logger.logInfo("Listening");

            while (running) {
                running = startServer(serverSocket);
            }

        } catch (IOException ex) {
            logger.logInfo(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private boolean startServer(ServerSocket serverSocket) {
        boolean result = true;
        try {
            Socket socket = serverSocket.accept();
            logger.logInfo("New client connected");

            serverThread = Factory.createServerThread(Constants.BASE_SERVER_THREAD, socket, squareController, logger);
            serverThread.start();
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }

        return result;
    }
}

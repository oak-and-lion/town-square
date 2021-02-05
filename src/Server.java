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
    private IFactory factory;
    private IApp parent;

    public static IServer create(int port, ISquareController controller, ILogIt logger, IFactory factory, IApp parent) {
        if (iserver == null) {
            iserver = new Server(port, controller, logger, factory, parent);
        }
        return iserver;
    }

    public int getPort() {
        return port;
    }

    private Server(int port, ISquareController controller, ILogIt logger, IFactory factory, IApp parent) {
        running = false;
        this.port = port;
        this.squareController = controller;
        this.logger = logger;
        this.factory = factory;
        this.parent = parent;
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

            logger.logInfo("Listening: " + Integer.toString(port));

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
            String clientIP = socket.getInetAddress().getHostAddress();
            logger.logInfo("New client connected: " + clientIP);

            if (parent.isHidingServer()) {
                logger.logInfo("Not serving requests right now.");
            }
            
            serverThread = factory.createServerThread(Constants.BASE_SERVER_THREAD, socket, squareController, logger,
                    factory.createUtility(Constants.BASE_UTILITY));
            serverThread.start();

        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }

        return result;
    }
}

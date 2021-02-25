import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Server extends Thread implements IServer {
    private int port;
    private boolean running;
    private static IServer iserver;
    private IServerThread serverThread;
    private ISquareController squareController;
    private ILogIt logger;
    private ILogIt errorLogger;
    private IFactory factory;
    private IApp parent;
    private IUtility utility;

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
        if (this.logger.getDialogController().getFactory() == null) {
            this.logger.getDialogController().setFactory(this.factory);
        }
        this.parent = parent;
        this.utility = factory.createUtility(Constants.BASE_UTILITY, parent.getDialogController());
        this.errorLogger = factory.createLogger(Constants.ERROR_LOGGER, Constants.ERROR_LOG_FILE, utility,
                this.logger.getDialogController());
    }

    public void teardown() {
        logger.logInfo("Ending Server");
        running = false;

        serverThread = null;
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            try (ServerSocket serverSocket = new ServerSocket(port)) {

                logger.logInfo(utility.concatStrings("Listening: ", Integer.toString(port)));

                running = startServer(serverSocket);

            } catch (IOException ex) {
                errorLogger.logInfo(
                        utility.concatStrings(ex.getMessage(), Constants.NEWLINE, Arrays.toString(ex.getStackTrace())));
                parent.closeApp(Constants.SYSTEM_EXIT_PORT_IN_USE, Constants.SYSTEM_EXIT_PORT_IN_USE);
            }
        }
    }

    private boolean startServer(ServerSocket serverSocket) {
        boolean result = true;
        try {
            Socket socket = serverSocket.accept();
            String clientIP = socket.getInetAddress().getHostAddress();
            logger.logInfo(utility.concatStrings("New client connected: ", clientIP));

            RequesterInfo requester = new RequesterInfo(clientIP);

            if (parent.isHidingServer()) {
                logger.logInfo("Not serving requests right now.");
            }

            if (logger.getDialogController().getFactory() == null) {
                logger.getDialogController().setFactory(factory);
            }

            serverThread = factory.createServerThread(Constants.BASE_SERVER_THREAD, socket, squareController, logger,
                    factory.createUtility(Constants.BASE_UTILITY, parent.getDialogController()), requester);
            serverThread.start();

        } catch (Exception e) {
            errorLogger.logInfo(
                    utility.concatStrings(e.getMessage(), Constants.NEWLINE, Arrays.toString(e.getStackTrace())));
        }

        return result;
    }
}

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Server extends Thread implements IServer {
    private int port;
    private boolean running;
    private static IServer iserver;
    private ArrayList<IServerThread> serverThreads;
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
        this.serverThreads = new ArrayList<>();
    }

    public void teardown() {
        logger.logInfo("Ending Server");
        running = false;
        serverThreads.clear();
    }

    private Boolean isRunning() {
        return running;
    }

    private void startRunning() {
        setRunning(true);
    }

    private void setRunning(Boolean value) {
        running = value;
    }

    @Override
    public void run() {
        startRunning();

        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(port));
            while (isRunning()) {
                logger.logInfo(utility.concatStrings("Listening: ", Integer.toString(port)));

                setRunning(startServer(serverSocket));

                ArrayList<IServerThread> doneThreads = new ArrayList<>();
                for (IServerThread s : serverThreads) {
                    if (s.isDone()) {
                        doneThreads.add(s);
                    }
                }
                for (IServerThread s : doneThreads) {
                    s.closeSocket();
                    serverThreads.remove(s);
                }
                doneThreads.clear();
            }
        } catch (IOException ex) {
            errorLogger.logInfo(
                    utility.concatStrings(ex.getMessage(), Constants.NEWLINE, Arrays.toString(ex.getStackTrace())));
            parent.closeApp(Constants.SYSTEM_EXIT_PORT_IN_USE, Constants.SYSTEM_EXIT_PORT_IN_USE);
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

            IServerThread serverThread = factory.createServerThread(Constants.BASE_SERVER_THREAD, socket, squareController, logger,
                    factory.createUtility(Constants.BASE_UTILITY, parent.getDialogController()), requester);
            serverThreads.add(serverThread);
            serverThread.start();
        } catch (Exception e) {
            errorLogger.logInfo(
                    utility.concatStrings(e.getMessage(), Constants.NEWLINE, Arrays.toString(e.getStackTrace())));
        }

        return result;
    }
}

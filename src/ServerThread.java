import java.io.*;
import java.net.*;
import java.util.Arrays;

/**
 * This thread is responsible to handle client connection.
 *
 * @author www.codejava.net
 */
public class ServerThread extends Thread implements IServerThread {
    private Socket socket;
    private ISquareController controller;
    private ILogIt logger;
    private IUtility utility;
    private RequesterInfo requester;
    private ILogIt errorLogger;
    private IFactory factory;
    private boolean done;

    public ServerThread(Socket socket, ISquareController squareController, ILogIt logger, IUtility utility,
            RequesterInfo requester) {
        this.socket = socket;
        this.controller = squareController;
        this.logger = logger;
        this.utility = utility;
        this.requester = requester;
        this.factory = logger.getDialogController().getFactory();
        this.errorLogger = factory.createLogger(Constants.ERROR_LOGGER, Constants.ERROR_LOG_FILE, utility,
                logger.getDialogController());
    }

    public Boolean isDone() {
        return done;
    }

    public void closeSocket() {
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                done = true;
                errorLogger.logInfo(
                        utility.concatStrings(e.getMessage(), Constants.NEWLINE, Arrays.toString(e.getStackTrace())));
            }
        }
    }

    @Override
    public void run() {
        done = false;
        try (InputStream input = socket.getInputStream()) {
            buildReader(input);
        } catch (IOException ex) {
            if (!socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    done = true;
                    errorLogger.logInfo(utility.concatStrings(e.getMessage(), Constants.NEWLINE,
                            Arrays.toString(e.getStackTrace())));
                }
            }
            done = true;
            errorLogger.logInfo(
                    utility.concatStrings(ex.getMessage(), Constants.NEWLINE, Arrays.toString(ex.getStackTrace())));
        }
        done = true;
    }

    private void buildReader(InputStream input) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            buildOutputStream(reader);
        } catch (IOException ex) {
            if (!socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    done = true;
                    errorLogger.logInfo(utility.concatStrings(e.getMessage(), Constants.NEWLINE,
                            Arrays.toString(e.getStackTrace())));
                }
            }
            done = true;
            errorLogger.logInfo(
                    utility.concatStrings(ex.getMessage(), Constants.NEWLINE, Arrays.toString(ex.getStackTrace())));
        }
    }

    private void buildOutputStream(BufferedReader reader) {
        try (OutputStream output = socket.getOutputStream()) {
            a(output, reader);
        } catch (IOException ex) {
            if (!socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    done = true;
                    errorLogger.logInfo(utility.concatStrings(e.getMessage(), Constants.NEWLINE,
                            Arrays.toString(e.getStackTrace())));
                }
            }
            done = true;
            errorLogger.logInfo(
                    utility.concatStrings(ex.getMessage(), Constants.NEWLINE, Arrays.toString(ex.getStackTrace())));
        }
    }

    private void a(OutputStream output, BufferedReader reader) {
        try (PrintWriter writer = new PrintWriter(output, true)) {

            String text;

            do {
                text = reader.readLine();
                if (text != null && !text.equals("bye") && !controller.isHiding()) {
                    SquareResponse response = controller.processRequest(text, requester);
                    writer.println(response.toString());
                } else if (controller.isHiding()) {
                    writer.println(utility.generateRandomString(52));
                } else {
                    writer.println("200:terminated");
                }

            } while (text != null && !text.equals("bye"));
            logger.logInfo(utility.concatStrings("Client Disconnected: ", socket.getInetAddress().getHostAddress()));
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ex) {
            String add = socket.getRemoteSocketAddress().toString();
            if (!socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    done = true;
                    errorLogger.logInfo(utility.concatStrings("[", add, "] - close socket error ", e.getMessage(),
                            Constants.NEWLINE, Arrays.toString(e.getStackTrace())));
                }
            }
            done = true;
            if (ex.getMessage().contains("Connection reset")) {
                if (controller.getLogger().getDialogController().getParent().isDebug()) {
                    utility.logError(utility.concatStrings("[", add, "] - Connection reset: ", ex.getMessage(), Constants.NEWLINE,
                        Arrays.toString(ex.getStackTrace())));
                }
            } else if (ex.getMessage().contains("No route to host")) {
                if (controller.getLogger().getDialogController().getParent().isDebug()) {
                    utility.logError(utility.concatStrings("[", add, "] I/P error: ", ex.getMessage(), Constants.NEWLINE,
                        Arrays.toString(ex.getStackTrace())));
                }
            } else {
                errorLogger.logInfo(utility.concatStrings("[", add, "] - I/O error: ", ex.getMessage(), Constants.NEWLINE,
                    Arrays.toString(ex.getStackTrace())));
            }
        }
    }
}

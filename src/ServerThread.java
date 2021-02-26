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

    @Override
    public void run() {
        try (InputStream input = socket.getInputStream()) {
            buildReader(input);
        } catch (IOException ex) {
            if (!socket.isClosed()) {
                try {
                    socket.close();
                } catch (Exception e) {
                    errorLogger.logInfo(utility.concatStrings(e.getMessage(), Constants.NEWLINE,
                            Arrays.toString(e.getStackTrace())));
                }
            }
            errorLogger.logInfo(
                    utility.concatStrings(ex.getMessage(), Constants.NEWLINE, Arrays.toString(ex.getStackTrace())));
        }
    }

    private void buildReader(InputStream input) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            buildOutputStream(reader);
        } catch (IOException ex) {
            if (!socket.isClosed()) {
                try {
                    socket.close();
                } catch (Exception e) {
                    errorLogger.logInfo(utility.concatStrings(e.getMessage(), Constants.NEWLINE,
                            Arrays.toString(e.getStackTrace())));
                }
            }
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
                } catch (Exception e) {
                    errorLogger.logInfo(utility.concatStrings(e.getMessage(), Constants.NEWLINE,
                            Arrays.toString(e.getStackTrace())));
                }
            }
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
            if (!socket.isClosed()) {
                try {
                    socket.close();
                } catch (Exception e) {
                    errorLogger.logInfo(utility.concatStrings(e.getMessage(), Constants.NEWLINE,
                            Arrays.toString(e.getStackTrace())));
                }
            }
            errorLogger.logInfo(
                    utility.concatStrings(ex.getMessage(), Constants.NEWLINE, Arrays.toString(ex.getStackTrace())));
        }
    }
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client implements IClient {
    private int port;
    private String hostName;
    private String squareId;
    private ILogIt logger;

    private static final String CLIENT_PREFIX = "Client '";

    public Client(String hostName, int port, String squareId) {
        this.hostName = hostName;
        this.port = port;
        this.squareId = squareId;
        createLogger();
    }

    public Client(Square square) {
        port = Integer.valueOf(square.getPort());
        hostName = square.getIP();
        squareId = square.getId();
        createLogger();
    }

    private void createLogger() {
        logger = Factory.createLogger(Constants.FILE_LOGGER, Constants.CLIENT_LOG_PREFIX + squareId + Constants.LOG_FILE_EXT, Factory.createUtility(Constants.BASE_UTILITY));
    }

    public String sendMessage(String text, boolean encrypt) {        
        logger.logInfo("Sending client request: [" + hostName + Constants.COLON + "] " + text);
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(hostName, port), 1000);
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String encryptFlag = "u";
            if (encrypt) {
                encryptFlag = "e";
            }

            return communicateWithServer(
                    encryptFlag + Constants.COMMAND_DATA_SEPARATOR + squareId + Constants.COMMAND_DATA_SEPARATOR + text, writer, socket);
        } catch (SocketException se) {
            if (se.getMessage().equals("Connection refused: connect")) {
                logger.logInfo(CLIENT_PREFIX + squareId + "' not available");
            } else if (se.getMessage().equals("Connection timed out: connect")) {
                logger.logInfo(CLIENT_PREFIX + hostName + Constants.COLON + Integer.toString(port) + "' not available");
            } else {
                logger.logInfo(CLIENT_PREFIX + hostName + Constants.COLON + Integer.toString(port) + "' " + se.getMessage());
            }
        } catch (UnknownHostException ex) {
            logger.logInfo("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            logger.logInfo("I/O error: " + ex.getMessage());
        } catch (Exception e) {
            logger.logInfo(CLIENT_PREFIX + hostName + Constants.COLON + Integer.toString(port) + "' " + e.getMessage());
        }

        return Constants.EMPTY_STRING;
    }

    public String getPort() {
        return Integer.toString(port);
    }

    public String getHostname() {
        return hostName;
    }

    public String getSquareId() {
        return squareId;
    }

    private String communicateWithServer(String text, PrintWriter writer, Socket socket) {
        try {
            writer.println(text);
            InputStream input = socket.getInputStream();
            return readServerReply(input);
        } catch (Exception e) {
            logger.logInfo(CLIENT_PREFIX + hostName + Constants.COLON + Integer.toString(port) + "' " + e.getMessage());
        }

        return Constants.EMPTY_STRING;
    }

    private String readServerReply(InputStream input) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            return reader.readLine();
        } catch (IOException ioe) {
            logger.logInfo(CLIENT_PREFIX + hostName + Constants.COLON + Integer.toString(port) + "' " + ioe.getMessage());
        }

        return Constants.EMPTY_STRING;
    }
}

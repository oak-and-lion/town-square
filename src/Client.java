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
    private IUtility utility;

    private static final String CLIENT_PREFIX = "Client '";

    public Client(String hostName, int port, String squareId, IFactory factory) {
        this.hostName = hostName;
        this.port = port;
        this.squareId = squareId;
        createLogger(factory);
        this.utility = factory.createUtility(Constants.BASE_UTILITY);
    }

    public Client(Square square, IFactory factory) {
        port = Integer.valueOf(square.getPort());
        hostName = square.getIP();
        squareId = square.getId();
        createLogger(factory);
    }

    private void createLogger(IFactory factory) {
        logger = factory.createLogger(Constants.FILE_LOGGER,
                utility.concatStrings(Constants.CLIENT_LOG_PREFIX, squareId, Constants.LOG_FILE_EXT),
                factory.createUtility(Constants.BASE_UTILITY));
    }

    public String sendMessage(String text, boolean encrypt) {
        logger.logInfo(utility.concatStrings("Sending client request: [", hostName, Constants.COLON,
                Integer.toString(port), "] ", text));
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(hostName, port), 1000);
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String encryptFlag = Constants.UNENCRYPTED_FLAG;
            if (encrypt) {
                encryptFlag = Constants.ENCRYPTED_FLAG;
            }

            return communicateWithServer(utility.concatStrings(encryptFlag, Constants.COMMAND_DATA_SEPARATOR, squareId,
                    Constants.COMMAND_DATA_SEPARATOR, text), writer, socket);
        } catch (SocketException se) {
            if (se.getMessage().equals("Connection refused: connect")) {
                logger.logInfo(utility.concatStrings(CLIENT_PREFIX, squareId, "' not available"));
            } else if (se.getMessage().equals("Connection timed out: connect")) {
                logger.logInfo(utility.concatStrings(CLIENT_PREFIX, hostName, Constants.COLON, Integer.toString(port),
                        "' not available"));
            } else {
                logger.logInfo(utility.concatStrings(CLIENT_PREFIX, hostName, Constants.COLON, Integer.toString(port),
                        Constants.SINGLE_QUOTE, Constants.SPACE, se.getMessage()));
            }
        } catch (UnknownHostException ex) {
            logger.logInfo(utility.concatStrings("Server not found: ", ex.getMessage()));
        } catch (IOException ex) {
            logger.logInfo(utility.concatStrings("I/O error: ", ex.getMessage()));
        } catch (Exception e) {
            logger.logInfo(utility.concatStrings(CLIENT_PREFIX, hostName, Constants.COLON, Integer.toString(port),
                    Constants.SINGLE_QUOTE, Constants.SPACE, e.getMessage()));
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
            logger.logInfo(utility.concatStrings(CLIENT_PREFIX, hostName, Constants.COLON, Integer.toString(port),
                    Constants.SINGLE_QUOTE, Constants.SPACE, e.getMessage()));
        }

        return Constants.EMPTY_STRING;
    }

    private String readServerReply(InputStream input) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            return reader.readLine();
        } catch (IOException ioe) {
            logger.logInfo(utility.concatStrings(CLIENT_PREFIX, hostName, Constants.COLON, Integer.toString(port),
                    Constants.SINGLE_QUOTE, Constants.SPACE, ioe.getMessage()));
        }

        return Constants.EMPTY_STRING;
    }
}

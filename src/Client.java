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
import java.util.Arrays;

public class Client implements IClient {
    private int port;
    private String hostName;
    private String squareId;
    private ILogIt logger;
    private IUtility utility;
    private IApp appParent;

    private static final String CLIENT_PREFIX = "Client '";

    public Client(String hostName, int port, String squareId, IFactory factory, IApp appParent) {
        this.hostName = hostName;
        this.port = port;
        this.squareId = squareId;
        this.appParent = appParent;
        this.utility = factory.createUtility(Constants.BASE_UTILITY, appParent.getDialogController());
        createLogger(factory, appParent.getLoggerType());
    }

    public Client(Square square, IFactory factory) {
        port = Integer.valueOf(square.getPort());
        hostName = square.getIP();
        squareId = square.getId();
        this.utility = factory.createUtility(Constants.BASE_UTILITY, appParent.getDialogController());
        
        createLogger(factory, square.getSampleController().getParent().getLoggerType());
    }

    private void createLogger(IFactory factory, int loggerType) {
        logger = factory.createLogger(loggerType,
                utility.concatStrings(Constants.CLIENT_LOG_PREFIX, squareId, Constants.LOG_FILE_EXT),
                factory.createUtility(Constants.BASE_UTILITY, appParent.getDialogController()), appParent.getDialogController());
    }

    public String sendMessage(String text, boolean encrypt, String command) {
        String guid = utility.createUUID();
        String encryptFlag = Constants.UNENCRYPTED_FLAG;
        if (encrypt) {
            encryptFlag = Constants.ENCRYPTED_FLAG;
        }
        String sendData = utility.concatStrings(encryptFlag, Constants.COMMAND_DATA_SEPARATOR, squareId,
                Constants.COMMAND_DATA_SEPARATOR, text);
        logger.logInfo(utility.concatStrings(guid, " Sending client request: [", hostName, Constants.COLON,
                Integer.toString(port), "] ", command, Constants.SPACE, Constants.DASH, Constants.SPACE, sendData));
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(hostName, port), 1000);
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            String result = communicateWithServer(sendData, writer, socket);
            logger.logInfo(utility.concatStrings(guid, " Client request response: [", hostName, Constants.COLON,
                    Integer.toString(port), "] "));
            return result;
        } catch (SocketException se) {
            if (se.getMessage().equals("Connection refused: connect")) {
                utility.logError(
                        utility.concatStrings(guid, Constants.SPACE, CLIENT_PREFIX, squareId, "' not available", Constants.NEWLINE, Arrays.toString(se.getStackTrace())));
            } else if (se.getMessage().equals("Connection timed out: connect")) {
                utility.logError(utility.concatStrings(guid, Constants.SPACE, CLIENT_PREFIX, hostName, Constants.COLON,
                        Integer.toString(port), "' not available", Constants.NEWLINE, Arrays.toString(se.getStackTrace())));
            } else {
                utility.logError(utility.concatStrings(guid, Constants.SPACE, CLIENT_PREFIX, hostName, Constants.COLON,
                        Integer.toString(port), Constants.SINGLE_QUOTE, Constants.SPACE, se.getMessage(), Constants.NEWLINE, Arrays.toString(se.getStackTrace())));
            }
        } catch (UnknownHostException ex) {
            utility.logError(utility.concatStrings(guid, " Server not found: ", ex.getMessage(), Constants.NEWLINE, Arrays.toString(ex.getStackTrace())));
        } catch (IOException ex) {
            if (ex.getMessage().equals("connect timed out")) {
                // do nothing
            } else {
                utility.logError(utility.concatStrings(guid, " I/O error: ", ex.getMessage(), Constants.NEWLINE, Arrays.toString(ex.getStackTrace())));
            }
        } catch (Exception e) {
            utility.logError(utility.concatStrings(guid, Constants.SPACE, CLIENT_PREFIX, hostName, Constants.COLON,
                    Integer.toString(port), Constants.SINGLE_QUOTE, Constants.SPACE, e.getMessage(), Constants.NEWLINE, Arrays.toString(e.getStackTrace())));
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
                    Constants.SINGLE_QUOTE, Constants.SPACE, e.getMessage(), Constants.NEWLINE, Arrays.toString(e.getStackTrace())));
        }

        return Constants.EMPTY_STRING;
    }

    private String readServerReply(InputStream input) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            return reader.readLine();
        } catch (IOException ioe) {
            logger.logInfo(utility.concatStrings(CLIENT_PREFIX, hostName, Constants.COLON, Integer.toString(port),
                    Constants.SINGLE_QUOTE, Constants.SPACE, ioe.getMessage(), Constants.NEWLINE, Arrays.toString(ioe.getStackTrace())));
        }

        return Constants.EMPTY_STRING;
    }
}

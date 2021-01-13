import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {
    private static final String EMPTY_STRING = "";
    private static final String COMMAND_DATA_SEPARATOR = "%%%";

    private int port;
    private String hostName;
    private String squareId;

    public Client(String hostName, int port, String squareId) {
        this.hostName = hostName;
        this.port = port;
        this.squareId = squareId;
    }

    public Client(Square square) {
        port = Integer.valueOf(square.getPort());
        hostName = square.getIP();
        squareId = square.getId();
    }

    public String sendMessage(String text, boolean encrypt) {
        LogIt.LogInfo(text);
        try (Socket socket = new Socket(hostName, port)) {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String encryptFlag = "u";
            if (encrypt) {
                encryptFlag = "e";
            }

            return communicateWithServer(
                    encryptFlag + COMMAND_DATA_SEPARATOR + squareId + COMMAND_DATA_SEPARATOR + text, writer, socket);
        } catch (SocketException se) {
            se.printStackTrace();
        } catch (UnknownHostException ex) {
            LogIt.LogInfo("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            LogIt.LogInfo("I/O error: " + ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return EMPTY_STRING;
    }

    private String communicateWithServer(String text, PrintWriter writer, Socket socket) {
        try {
            writer.println(text);
            InputStream input = socket.getInputStream();
            return readServerReply(input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return EMPTY_STRING;
    }

    private String readServerReply(InputStream input) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            return reader.readLine();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return EMPTY_STRING;
    }
}

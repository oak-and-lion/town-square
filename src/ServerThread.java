import java.io.*;
import java.net.*;
 
/**
 * This thread is responsible to handle client connection.
 *
 * @author www.codejava.net
 */
public class ServerThread extends Thread implements IServerThread {
    private Socket socket;
    private ISquareController controller;
    private ILogIt logger;
 
    public ServerThread(Socket socket, ISquareController squareController, ILogIt logger) {
        this.socket = socket;
        this.controller = squareController;
        this.logger = logger;
    }
 
    @Override
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
 
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
 
            String text;
 
            do {
                text = reader.readLine();
                if (text != null && !text.equals("bye")) {
                    SquareResponse response = controller.processRequest(text);
                    writer.println(response.toString());
                } else {
                    writer.println("200:terminated");
                }
 
            } while (text != null && !text.equals("bye"));
            logger.logInfo("Client Disconnected");
            socket.close();
        } catch (IOException ex) {
            logger.logInfo(ex.getMessage());
            ex.printStackTrace();
        }
    }
}

import java.io.*;
import java.net.*;
 
/**
 * This thread is responsible to handle client connection.
 *
 * @author www.codejava.net
 */
public class ServerThread extends Thread {
    private Socket socket;
    private ISquareController controller;
 
    public ServerThread(Socket socket, ISquareController squareController) {
        this.socket = socket;
        this.controller = squareController;
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
            LogIt.LogInfo("Client Disconnected");
            socket.close();
        } catch (IOException ex) {
            LogIt.LogInfo(ex.getMessage());
            ex.printStackTrace();
        }
    }
}

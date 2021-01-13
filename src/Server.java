import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    private int port;
    private boolean running;
    private static Server server;
    private ServerThread serverThread;
    private ISquareController squareController;

    private Server(int port, ISquareController controller) {
        this.port = port;
        squareController = controller;
    }

    public static Server create(int port, ISquareController controller) {
        if (server == null) {
            server = new Server(port, controller);
        }
        return server;
    }

    public int getPort() {
        return port;
    }

    public Server() {
        running = false;
    }

    public void teardown() {
        LogIt.LogInfo("Ending Server");
        running = false;

        serverThread = null;
    }

    @Override
    public void run() {
        running = true;
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            LogIt.LogInfo("Listening");

            while (running) {
                running = startServer(serverSocket);
            }

        } catch (IOException ex) {
            LogIt.LogInfo(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private boolean startServer(ServerSocket serverSocket) {
        boolean result = true;
        try {
            Socket socket = serverSocket.accept();
            LogIt.LogInfo("New client connected");

            serverThread = new ServerThread(socket, squareController);
            serverThread.start();
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }

        return result;
    }
}

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.scene.*;
import javafx.stage.*;

public class App extends Application implements IApp {
    private static final String VERSION = "0.0.1";
    private static final String SQUARE_FILE_EXT = ".square";
    private static final String UNIQUE_ID_FILE = "unique.id";
    private static final String DEFAULT_NAME_FILE = "default.name";
    private static final String DEFAULT_SQUARE_FILE = "my_square.square";
    private static final String DEFAULT_SQUARE_ME_FILE = "my_square.members";
    private static final String PORT_FILE = "port.id";
    private static final String IP_FILE = "ip.id";
    private static final String FXML_FILE = "sample.fxml";
    private static final String PUBLIC_KEY_FILE = "public.key";
    private static final String PRIVATE_KEY_FILE = "private.key";
    private static final String LOCK_FILE = "town_square.lock";
    private static final String LOCK_FILE_CONTENTS = "lock";
    private static final String APP_TITLE = "Town Square";
    private static final String DEFAULT_SQUARE_NAME = "My Square";
    private static final String COMMA = ",";
    private static final String NOT_PRIVATE = "0";
    private static final String DEFAULT_PORT = "44123";
    private static final String EMPTY_STRING = "";
    private static final String NO_PASSWORD_VALUE = "~~~~~~~";
    private static final String DATA_SEPARATOR = "~_~";
    private static final String DEFAULT_IP = "127.0.0.1";

    private Utility utility;
    private Server server;
    private String defaultName;
    private SquareKeyPair keys;

    @Override
    public void start(Stage primaryStage) {
        String uniqueId;
        String defaultSquareInfo = "";
        Square defaultSquare = null;
        String port = DEFAULT_PORT;
        String ip = DEFAULT_IP;
        defaultName = EMPTY_STRING;

        SampleController controller = null;
        ISquareController squareController = null;
        CryptoUtils cryptoUtils = new CryptoUtils();

        keys = new SquareKeyPair();

        utility = Utility.create();

        checkCurrentState();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_FILE));

            Parent root = loader.load();

            Scene scene = new Scene(root);

            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(event -> {
                LogIt.LogInfo("Closing");
                close();
                System.exit(0);
            });

            if (!utility.checkFileExists(UNIQUE_ID_FILE)) {
                uniqueId = utility.createIdFile(UNIQUE_ID_FILE);
            } else {
                uniqueId = utility.readFile(UNIQUE_ID_FILE);
            }
            if (!utility.checkFileExists(PORT_FILE)) {
                utility.writeFile(PORT_FILE, port);
            } else {
                port = utility.readFile(PORT_FILE);
            }
            if (!utility.checkFileExists(IP_FILE)) {
                utility.writeFile(IP_FILE, utility.getRemoteIP());
            } else {
                ip = utility.readFile(IP_FILE);
            }
            if (utility.checkFileExists(DEFAULT_NAME_FILE)) {
                defaultName = utility.readFile(DEFAULT_NAME_FILE);
            }
            if (utility.checkFileExists(DEFAULT_SQUARE_FILE)) {
                defaultSquareInfo = utility.readFile(DEFAULT_SQUARE_FILE);
            } else {
                defaultSquareInfo = DEFAULT_SQUARE_NAME + COMMA + utility.createUUID() + COMMA + "tabDefaultSquare"
                        + COMMA + NOT_PRIVATE + COMMA + NO_PASSWORD_VALUE;
                utility.writeFile(DEFAULT_SQUARE_FILE, defaultSquareInfo);
            }
            if (utility.checkFileExists(PUBLIC_KEY_FILE) && utility.checkFileExists(PRIVATE_KEY_FILE)) {
                keys.setPublicKeyFromBase64(utility.readFile(PUBLIC_KEY_FILE));
                keys.setPrivateKeyFromBase64(utility.readFile(PRIVATE_KEY_FILE));
            } else {
                keys = cryptoUtils.generateKeyPair();
                utility.writeFile(PUBLIC_KEY_FILE, keys.getPublicKeyBase64());
                utility.writeFile(PRIVATE_KEY_FILE, keys.getPrivateKeyBase64());
            }
            if (!utility.checkFileExists(DEFAULT_SQUARE_ME_FILE)) {
                utility.writeFile(DEFAULT_SQUARE_ME_FILE, defaultName + DATA_SEPARATOR + keys.getPublicKeyBase64()
                        + DATA_SEPARATOR + utility.getRemoteIP() + DATA_SEPARATOR + port + DATA_SEPARATOR + uniqueId);
            }

            primaryStage.show();
            controller = loader.<SampleController>getController();

            squareController = SquareFactory.create(utility, controller);

            defaultSquare = new Square(defaultSquareInfo, port, ip, squareController, utility,
                    controller, uniqueId);

            ObservableList<IPAddress> ipAddresses = FXCollections.observableArrayList();
            ipAddresses.add(new IPAddress(defaultSquare.getIP(), defaultSquare.getIP()));
            ipAddresses.addAll(utility.getLocalIPs());

            controller.setParent(this);
            controller.setUniqueId(uniqueId);
            controller.setDefaultName(defaultName);
            controller.setVersion(VERSION);
            controller.setRemoteIP(ipAddresses, ip, utility);
            controller.setPort(port);
            controller.setTabSquare(defaultSquare);
            controller.setPublicKey(keys.getPublicKeyBase64());
            controller.buildSquares(utility);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        if (squareController != null) {
            server = Server.create(Integer.parseInt(port), squareController);
            server.start();
        }
    }

    private void checkCurrentState() {
        if (isRunning()) {
            AlertBox.createAlert("Already Running!", "An instance of Town Square is already running!",
                    "Close the other instance before you start a new instance.");
            System.exit(1);
        } else {
            utility.writeFile(LOCK_FILE, LOCK_FILE_CONTENTS);
        }
    }

    private boolean isRunning() {
        return utility.checkFileExists(LOCK_FILE);
    }

    @Override
    public void stop() {
        utility.deleteFile(LOCK_FILE);
        server.teardown();
    }

    public void close() {
        stop();
    }

    public void sendDefaultName(String defaultName) {
        utility.deleteFile(DEFAULT_NAME_FILE);
        utility.writeFile(DEFAULT_NAME_FILE, defaultName);
    }

    public void sendPort(String port) {
        utility.deleteFile(PORT_FILE);
        utility.writeFile(PORT_FILE, port);
    }

    public void updateSquare(Square square) {
        String name = square.getSafeLowerName() + SQUARE_FILE_EXT;
        utility.deleteFile(name);
        utility.writeFile(name, square.toString());
    }

    public String getDefaultName() {
        return defaultName;
    }

    public String getPublicKeyBase64() {
        return keys.getPublicKeyBase64();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
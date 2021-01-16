import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.scene.*;
import javafx.stage.*;

public class App extends Application implements IApp {
    private IUtility utility;
    private IServer server;
    private String defaultName;
    private ISquareKeyPair keys;

    @Override
    public void start(Stage primaryStage) {
        String uniqueId;
        String defaultSquareInfo = "";
        ISquare defaultSquare = null;
        String port = Constants.DEFAULT_PORT;
        String ip = Constants.DEFAULT_IP;
        defaultName = Constants.EMPTY_STRING;

        ISampleController controller = null;
        ISquareController squareController = null;
        ICryptoUtils cryptoUtils = Factory.createCryptoUtils(Constants.BASE_CRYPTO_UTILS);

        keys = Factory.createSquareKeyPair(Constants.BASE_SQUARE_KEY_PAIR);

        utility = Factory.createUtility(Constants.BASE_UTILITY);

        checkCurrentState();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_FILE));

            Parent root = loader.load();

            Scene scene = new Scene(root);

            primaryStage.setTitle(Constants.APP_TITLE);
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(event -> {
                LogIt.logInfo("Closing");
                close();
                System.exit(0);
            });

            if (!utility.checkFileExists(Constants.UNIQUE_ID_FILE)) {
                uniqueId = utility.createIdFile(Constants.UNIQUE_ID_FILE);
            } else {
                uniqueId = utility.readFile(Constants.UNIQUE_ID_FILE);
            }
            if (!utility.checkFileExists(Constants.PORT_FILE)) {
                utility.writeFile(Constants.PORT_FILE, port);
            } else {
                port = utility.readFile(Constants.PORT_FILE);
            }
            if (!utility.checkFileExists(Constants.IP_FILE)) {
                utility.writeFile(Constants.IP_FILE, utility.getRemoteIP());
            } else {
                ip = utility.readFile(Constants.IP_FILE);
            }
            if (utility.checkFileExists(Constants.DEFAULT_NAME_FILE)) {
                defaultName = utility.readFile(Constants.DEFAULT_NAME_FILE);
            }
            if (utility.checkFileExists(Constants.DEFAULT_SQUARE_FILE)) {
                defaultSquareInfo = utility.readFile(Constants.DEFAULT_SQUARE_FILE);
            } else {
                defaultSquareInfo = Constants.DEFAULT_SQUARE_NAME + Constants.COMMA + utility.createUUID() + Constants.COMMA + "tabDefaultSquare"
                        + Constants.COMMA + Constants.NOT_PRIVATE + Constants.COMMA + Constants.NO_PASSWORD_VALUE;
                utility.writeFile(Constants.DEFAULT_SQUARE_FILE, defaultSquareInfo);
            }
            if (utility.checkFileExists(Constants.PUBLIC_KEY_FILE) && utility.checkFileExists(Constants.PRIVATE_KEY_FILE)) {
                keys.setPublicKeyFromBase64(utility.readFile(Constants.PUBLIC_KEY_FILE));
                keys.setPrivateKeyFromBase64(utility.readFile(Constants.PRIVATE_KEY_FILE));
            } else {
                keys = cryptoUtils.generateKeyPair();
                utility.writeFile(Constants.PUBLIC_KEY_FILE, keys.getPublicKeyBase64());
                utility.writeFile(Constants.PRIVATE_KEY_FILE, keys.getPrivateKeyBase64());
            }
            if (!utility.checkFileExists(Constants.DEFAULT_SQUARE_ME_FILE)) {
                utility.writeFile(Constants.DEFAULT_SQUARE_ME_FILE, defaultName + Constants.DATA_SEPARATOR + keys.getPublicKeyBase64()
                        + Constants.DATA_SEPARATOR + utility.getRemoteIP() + Constants.DATA_SEPARATOR + port + Constants.DATA_SEPARATOR + uniqueId);
            }

            primaryStage.show();
            controller = loader.<SampleController>getController();

            squareController = Factory.createSquareController(Constants.BASE_SQUARE_CONTROLLER, utility, controller);

            defaultSquare = Factory.createSquare(Constants.BASE_SQUARE, defaultSquareInfo, port, ip, squareController, utility, controller, uniqueId);

            ObservableList<IPAddress> ipAddresses = FXCollections.observableArrayList();
            ipAddresses.add(new IPAddress(defaultSquare.getIP(), defaultSquare.getIP()));
            ipAddresses.addAll(utility.getLocalIPs());

            controller.setUtilityController(utility);
            controller.setParent(this);
            controller.setUniqueId(uniqueId);
            controller.setDefaultName(defaultName);
            controller.setVersion(Constants.VERSION);
            controller.setRemoteIP(ipAddresses, ip);
            controller.setPort(port);
            controller.setTabSquare(defaultSquare);
            controller.setPublicKey(keys.getPublicKeyBase64());
            controller.buildSquares();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        if (squareController != null) {
            server = Factory.createServer(Constants.BASE_SERVER, Integer.parseInt(port), squareController);
            server.start();
        }
    }

    private void checkCurrentState() {
        if (isRunning()) {
            AlertBox.createAlert("Already Running!", "An instance of Town Square is already running!",
                    "Close the other instance before you start a new instance.");
            System.exit(1);
        } else {
            utility.writeFile(Constants.LOCK_FILE, Constants.LOCK_FILE_CONTENTS);
        }
    }

    private boolean isRunning() {
        return utility.checkFileExists(Constants.LOCK_FILE);
    }

    @Override
    public void stop() {
        utility.deleteFile(Constants.LOCK_FILE);
        server.teardown();
    }

    public void close() {
        stop();
    }

    public void sendDefaultName(String defaultName) {
        utility.deleteFile(Constants.DEFAULT_NAME_FILE);
        utility.writeFile(Constants.DEFAULT_NAME_FILE, defaultName);
    }

    public void sendPort(String port) {
        utility.deleteFile(Constants.PORT_FILE);
        utility.writeFile(Constants.PORT_FILE, port);
    }

    public void updateSquare(Square square) {
        String name = square.getSafeLowerName() + Constants.SQUARE_FILE_EXT;
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
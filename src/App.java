import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.*;
import javafx.stage.*;

public class App extends Application implements IApp {
    private IUtility utility;
    private IServer server;
    private String defaultName;
    private ISquareKeyPair keys;
    private static IAlertBox alert;
    private static ISystemExit systemExit;

    private static void setUpDependencies(IAlertBox alertbox, ISystemExit exit) {
        alert = alertbox;
        systemExit = exit;
    }

    @Override
    public void start(Stage primaryStage) {
        utility = Factory.createUtility(Constants.BASE_UTILITY);

        if (checkCurrentState(alert)) {
            processStart(primaryStage);
        }
    }

    private void processStart(Stage primaryStage) {
        String uniqueId = Constants.EMPTY_STRING;
        String defaultSquareInfo = Constants.EMPTY_STRING;
        ISquare defaultSquare = null;
        String port = Constants.DEFAULT_PORT;
        String ip = Constants.DEFAULT_IP;
        String alias = Constants.EMPTY_STRING;
        defaultName = Constants.DEFAULT_USER_NAME;

        IDialogController controller = null;
        ISquareController squareController = null;
        IVersionChecker versionChecker;

        ICryptoUtils cryptoUtils = Factory.createCryptoUtils(Constants.BASE_CRYPTO_UTILS);

        keys = Factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility);

        utility = Factory.createUtility(Constants.BASE_UTILITY);

        ILogIt logger = Factory.createLogger(Constants.FILE_LOGGER, Constants.MAIN_LOG_FILE, utility);

        systemExit.setParent(this);
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_FILE));

            Parent root = loader.load();

            Scene scene = new Scene(root);

            primaryStage.setTitle(Constants.APP_TITLE);
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(event -> {
                logger.logInfo("Closing");
                close();
                systemExit.handleExit(Constants.GRACEFUL_SHUTDOWN);
            });

            cleanup();

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
                defaultSquareInfo = Constants.DEFAULT_SQUARE_NAME + Constants.COMMA + utility.createUUID()
                        + Constants.COMMA + "tabDefaultSquare" + Constants.COMMA + Constants.NOT_PRIVATE
                        + Constants.COMMA + Constants.NO_PASSWORD_VALUE;
                utility.writeFile(Constants.DEFAULT_SQUARE_FILE, defaultSquareInfo);
            }
            if (utility.checkFileExists(Constants.PUBLIC_KEY_FILE)
                    && utility.checkFileExists(Constants.PRIVATE_KEY_FILE)) {
                keys.setPublicKeyFromBase64(utility.readFile(Constants.PUBLIC_KEY_FILE));
                keys.setPrivateKeyFromBase64(utility.readFile(Constants.PRIVATE_KEY_FILE));
            } else {
                keys = cryptoUtils.generateKeyPair();
                utility.writeFile(Constants.PUBLIC_KEY_FILE, keys.getPublicKeyBase64());
                utility.writeFile(Constants.PRIVATE_KEY_FILE, keys.getPrivateKeyBase64());
            }
            if (!utility.checkFileExists(Constants.DEFAULT_SQUARE_ME_FILE)) {
                utility.writeFile(Constants.DEFAULT_SQUARE_ME_FILE,
                        defaultName + Constants.DATA_SEPARATOR + keys.getPublicKeyBase64() + Constants.DATA_SEPARATOR
                                + utility.getRemoteIP() + Constants.DATA_SEPARATOR + port + Constants.DATA_SEPARATOR
                                + uniqueId);
            }

            primaryStage.show();
            controller = loader.<DialogController>getController();

            squareController = Factory.createSquareController(Constants.BASE_SQUARE_CONTROLLER, utility, controller,
                    Factory.createLogger(Constants.FILE_LOGGER, Constants.SQUARE_CONTROLLER_LOG_FILE, utility),
                    Factory.createSquareKeyPair(Constants.BASE_SQUARE_KEY_PAIR, utility));

            defaultSquare = Factory.createSquare(Constants.BASE_SQUARE, defaultSquareInfo, port, ip, squareController,
                    utility, controller, uniqueId);

            ObservableList<IPAddress> ipAddresses = FXCollections.observableArrayList();
            ipAddresses.add(new IPAddress(utility.getRemoteIP(), utility.getRemoteIP()));
            ipAddresses.addAll(utility.getLocalIPs());

            alias = getAlias(ip);

            initializeController(controller, uniqueId, port, ip, ipAddresses, alias, defaultSquare);

        } catch (IOException ioe) {
            logger.logInfo(ioe.getMessage());
            systemExit.handleExit(Constants.SYSTEM_EXIT_FAIL);
        }

        initializeSquareController(squareController, port);

        logger.logInfo("Started Town Square");

        controller.processPendingInvites();

        versionChecker = Factory.createVersionChecker(Constants.BASE_VERSION_CHECKER, utility, uniqueId);
        versionChecker.run();
    }

    private void initializeSquareController(ISquareController squareController, String port) {
        if (squareController != null) {
            server = Factory.createServer(Constants.BASE_SERVER, Integer.parseInt(port), squareController,
                    Factory.createLogger(Constants.FILE_LOGGER, Constants.SERVER_LOG_FILE, utility));
            server.start();
        }
    }

    private void initializeController(IDialogController controller, String uniqueId, String port, String ip,
            ObservableList<IPAddress> ipAddresses, String alias, ISquare defaultSquare) {
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
        controller.setAlias(alias);
    }

    private String getAlias(String ip) {
        String alias = ip;
        if (!utility.checkFileExists(Constants.ALIAS_FILE)) {
            utility.writeFile(Constants.ALIAS_FILE, ip);
        } else {
            alias = utility.readFile(Constants.ALIAS_FILE);
        }

        return alias;
    }

    private boolean checkCurrentState(IAlertBox alert) {
        if (isRunning()) {
            alert.createAlert("Already Running!", "An instance of Town Square is already running!",
                    "Close the other instance before you start a new instance.");
            systemExit.handleExit(Constants.SYSTEM_EXIT_FAIL);
            return false;
        } else {
            utility.writeFile(Constants.LOCK_FILE, Constants.LOCK_FILE_CONTENTS);
        }

        return true;
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

    private void cleanup() {
        utility.deleteFiles(Constants.LOG_FILE_EXT);
    }

    public void sendDefaultName(String defaultName) {
        utility.deleteFile(Constants.DEFAULT_NAME_FILE);
        utility.writeFile(Constants.DEFAULT_NAME_FILE, defaultName);
    }

    public void sendPort(String port) {
        utility.deleteFile(Constants.PORT_FILE);
        utility.writeFile(Constants.PORT_FILE, port);
    }

    public void sendIP(String ip, String oldIp, String uniqueId) {
        utility.writeFile(Constants.IP_FILE, ip);
        String[] files = utility.getFiles(Constants.MEMBERS_FILE_EXT);
        
        for (String file : files) {
            ArrayList<String> newLines = new ArrayList<String>();
            String[] lines = utility.readFile(file).split(Constants.READ_FILE_DATA_SEPARATOR);
            for (String line : lines) {
                if (line.contains(oldIp) && line.contains(uniqueId)) {
                    line = line.replace(oldIp, ip);
                }
                newLines.add(line);
            }
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (String line : newLines) {
                if (first) {
                    first = false;
                } else {
                    result.append(Constants.NEWLINE);
                }
                result.append(line);
            }
            utility.writeFile(file, result.toString());
        }
    }

    public void updateSquare(Square square) {
        String name = square.getSafeLowerName() + Constants.SQUARE_FILE_EXT;
        utility.deleteFile(name);
        utility.writeFile(name, square.toString());
    }

    public void sendAlias(String alias) {
        alias = alias.toLowerCase().trim().replace(Constants.SPACE, Constants.UNDERSCORE);
        utility.writeFile(Constants.ALIAS_FILE, alias);
    }

    public String getDefaultName() {
        return defaultName;
    }

    public String getPublicKeyBase64() {
        return keys.getPublicKeyBase64();
    }

    public static void main(String[] args) {
        setUpDependencies(Factory.createAlertBox(Constants.BASE_ALERT_BOX),
                Factory.createSystemExit(Constants.BASE_SYSTEM_EXIT));
        launch(args);
    }

    public static void execute(IAlertBox alertbox, ISystemExit systemExit) {
        setUpDependencies(alertbox, systemExit);
        App app = new App();
        app.start(null);
    }
}
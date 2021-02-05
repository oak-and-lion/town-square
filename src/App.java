import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javafx.scene.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.*;

public class App extends Application implements IApp {
    private IUtility utility;
    private IServer server;
    private String defaultName;
    private ISquareKeyPair keys;
    private ILogIt logger;
    private static IAlertBox alert;
    private static ISystemExit systemExit;
    private static IFactory factory;
    private boolean hidingServer;
    private String stageTitle;
    private Stage theStage;

    private static void setUpDependencies(IAlertBox alertbox, ISystemExit exit, IFactory f) {
        alert = alertbox;
        systemExit = exit;
        factory = f;
    }

    @Override
    public void start(Stage primaryStage) {
        utility = factory.createUtility(Constants.BASE_UTILITY);
        systemExit.setParent(this);
        if (checkCurrentState(alert)) {
            processStart(primaryStage);
        }
    }

    private IDialogController processStart(Stage primaryStage) {
        this.theStage = primaryStage;
        logger = factory.createLogger(Constants.FILE_LOGGER, Constants.MAIN_LOG_FILE, utility);
        String uniqueId = Constants.EMPTY_STRING;
        String defaultSquareInfo = Constants.EMPTY_STRING;
        ISquare defaultSquare = null;
        String port = Constants.DEFAULT_PORT;
        String ip = Constants.DEFAULT_IP;
        String alias = Constants.EMPTY_STRING;
        String remoteIP = utility.getRemoteIP(logger);
        defaultName = Constants.DEFAULT_USER_NAME;
        hidingServer = false;

        ISquareController squareController = null;
        IVersionChecker versionChecker;
        ICommandController commandController;

        ICryptoUtils cryptoUtils = factory.createCryptoUtils(Constants.BASE_CRYPTO_UTILS);

        keys = factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility);

        utility = factory.createUtility(Constants.BASE_UTILITY);

        systemExit.setParent(this);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_FILE));

            Parent root = loader.load();

            Scene scene = new Scene(root);

            final IDialogController controller = loader.<DialogController>getController();

            commandController = factory.createCommandController(Constants.BASE_COMMAND_CONTROLLER, utility, controller);

            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(event -> closeApp(Constants.SYSTEM_EXIT_OK, Constants.GRACEFUL_SHUTDOWN));

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
                utility.writeFile(Constants.IP_FILE, remoteIP);
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
                        + Constants.COMMA + Constants.DEFAULT_SQUARE_TAB_NAME + Constants.COMMA + Constants.NOT_PRIVATE
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
                                + remoteIP + Constants.DATA_SEPARATOR + port + Constants.DATA_SEPARATOR + uniqueId);
            }
            alias = getAliases(uniqueId);

            primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent window) {
                    Stage stage = (Stage) window.getSource();
                    setResizeListeners(stage, controller);
                }
            });

            stageTitle = Constants.APP_TITLE + Constants.SPACE + Constants.OPEN_PARENS + defaultName
                    + Constants.CLOSE_PARENS;

            primaryStage.setTitle(stageTitle);
            primaryStage.show();

            squareController = factory.createSquareController(Constants.BASE_SQUARE_CONTROLLER, utility, controller,
                    factory.createLogger(Constants.FILE_LOGGER, Constants.SQUARE_CONTROLLER_LOG_FILE, utility),
                    factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility));

            defaultSquare = factory.createSquare(Constants.BASE_SQUARE, defaultSquareInfo, port, ip, squareController,
                    utility, controller, uniqueId);

            ObservableList<IPAddress> ipAddresses = FXCollections.observableArrayList();
            ipAddresses.add(new IPAddress(remoteIP, remoteIP));
            ipAddresses.addAll(utility.getLocalIPs(logger));

            initializeController(controller, uniqueId, port, ip, ipAddresses, alias, defaultSquare, commandController);

            initializeSquareController(squareController, port);

            logger.logInfo("Started Town Square");

            controller.processPendingInvites();

            versionChecker = factory.createVersionChecker(Constants.BASE_VERSION_CHECKER, utility, uniqueId);
            versionChecker.run();

            return controller;

        } catch (IOException ioe) {
            logger.logInfo(ioe.getMessage());
            systemExit.handleExit(Constants.SYSTEM_EXIT_FAIL);
        }

        return null;
    }

    private void setResizeListeners(Stage primaryStage, IDialogController controller) {
        // create a listener
        final ChangeListener<Number> listener = new ChangeListener<Number>() {
            final Timer timer = new Timer(); // uses a timer to call your resize method
            TimerTask task = null; // task to execute after defined delay
            static final long DELAY_TIME = 500; // delay that has to pass in order to consider an operation done

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue) {
                if (task != null) { // there was already a task scheduled from the previous operation ...
                    task.cancel(); // cancel it, we have a new size to consider
                }

                task = new TimerTask() // create new task that calls your resize operation
                {
                    @Override
                    public void run() {
                        // here you can place your resize code
                        if (primaryStage.getWidth() < Constants.MIN_WINDOW_WIDTH) {
                            primaryStage.setWidth(Constants.MIN_WINDOW_WIDTH);
                        }

                        if (primaryStage.getHeight() < Constants.MIN_WINDOW_HEIGHT) {
                            primaryStage.setHeight(Constants.MIN_WINDOW_HEIGHT);
                        }
                        controller.resizeControls(primaryStage.getWidth(), primaryStage.getHeight());
                    }
                };
                // schedule new task
                timer.schedule(task, DELAY_TIME);
            }
        };

        // finally we have to register the listener
        primaryStage.widthProperty().addListener(listener);
        primaryStage.heightProperty().addListener(listener);
    }

    private void initializeSquareController(ISquareController squareController, String port) {
        if (squareController != null) {
            server = factory.createServer(Constants.BASE_SERVER, Integer.parseInt(port), squareController,
                    factory.createLogger(Constants.FILE_LOGGER, Constants.SERVER_LOG_FILE, utility));
            server.start();
        }
    }

    private void initializeController(IDialogController controller, String uniqueId, String port, String ip,
            ObservableList<IPAddress> ipAddresses, String alias, ISquare defaultSquare,
            ICommandController commandController) {
        controller.setFactory(factory);
        controller.setUtilityController(utility);
        controller.setCommandController(commandController);
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

    private boolean checkCurrentState(IAlertBox alert) {
        if (isRunning()) {
            alert.createAlert("Already Running!", "An instance of Town Square is already running!",
                    "Close the other instance before you start a new instance.", AlertType.INFORMATION);
            systemExit.handleExit(Constants.SYSTEM_EXIT_ALREADY_RUNNING);
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
        if (server != null) {
            server.teardown();
        }
    }

    public void close(int exitCode) {
        stop();
        if (exitCode != Constants.SYSTEM_EXIT_ALREADY_RUNNING) {
            utility.deleteFile(Constants.LOCK_FILE);
        }
    }

    public void closeApp(int exitCode, int shutdownCode) {
        logger.logInfo(Constants.CLOSING_LOG_MESSAGE);
        close(exitCode);
        systemExit.handleExit(shutdownCode);
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
            ArrayList<String> newLines = new ArrayList<>();
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

    public String getDefaultName() {
        return defaultName;
    }

    public String getPublicKeyBase64() {
        return keys.getPublicKeyBase64();
    }

    public void hideServer() {
        hidingServer = true;
        this.theStage.setTitle(stageTitle + Constants.SERVER_HIDING_TITLE);
    }

    public void exposeServer() {
        if (hidingServer) {
            hidingServer = false;
            this.theStage.setTitle(stageTitle);
        }
    }

    public boolean isHidingServer() {
        return hidingServer;
    }

    private String getAliases(String uniqueId) {
        if (utility.checkFileExists(Constants.MY_SQUARE_DEFAULT + Constants.ALIAS_FILE_EXT)) {
            String[] temp = utility.searchFile(Constants.MY_SQUARE_DEFAULT + Constants.ALIAS_FILE_EXT, uniqueId,
                    Constants.SEARCH_STARTS_WITH);
            if (temp.length > 0) {
                StringBuilder result = new StringBuilder();
                String[] values = temp[0].split(Constants.QUESTION_MARK_SPLIT);
                String[] ips = values[1].split(Constants.FORWARD_SLASH);
                boolean first = true;
                for (String ip : ips) {
                    String[] t = ip.split(Constants.COLON);
                    if (!first) {
                        result.append(Constants.SEMI_COLON);
                    }
                    first = false;
                    result.append(t[0]);
                }
                return result.toString();
            }
        }

        return Constants.EMPTY_STRING;
    }

    public static void main(String[] args) {
        IFactory factory = new Factory();
        setUpDependencies(factory.createAlertBox(Constants.BASE_ALERT_BOX),
                factory.createSystemExit(Constants.BASE_SYSTEM_EXIT), factory);
        launch(args);
    }

    public static void execute(IAlertBox alertbox, ISystemExit systemExit, IFactory factory) {
        setUpDependencies(alertbox, systemExit, factory);
        App app = new App();
        app.start(null);
    }
}
import java.util.ArrayList;

import javafx.stage.Stage;

public class AppBase extends Thread implements IApp {
    private int loggerType;
    private ISystemExit systemExit;
    private boolean hidingServer;
    private IUtility utility;
    private IServer server;
    private String port;
    private ILogIt logger;
    private ISquareKeyPair keyPair;
    private String defaultName;
    private Stage stage;
    private IDialogController controller;

    public AppBase(String loggerFlag, IAlertBox alertbox, ISystemExit exit, IFactory f) {
        setUpDependencies(alertbox, exit, f, loggerFlag);
    }

    private void setUpDependencies(IAlertBox alertbox, ISystemExit exit, IFactory factory, String logFlag) {
        systemExit = exit;
        loggerType = Constants.FILE_LOGGER;
        if (logFlag.equals("-nl")) {
            loggerType = Constants.EMPTY_LOGGER;
        } else if (logFlag.equals("-cl")) {
            loggerType = Constants.CONSOLE_LOGGER;
        }

        this.utility = factory.createUtility(Constants.BASE_UTILITY, new DialogControllerEmpty());
        controller = factory.createDialogController(Constants.SERVER_DIALOG_CONTROLLER, this, utility);
        
        if (!utility.checkFileExists(Constants.PORT_FILE)) {
            utility.writeFile(Constants.PORT_FILE, Constants.DEFAULT_PORT);
        }
        port = utility.readFile(Constants.PORT_FILE);
        controller.setUtilityController(utility);
        logger = factory.createLogger(loggerType, Constants.MAIN_LOG_FILE, utility, controller);
        ICommandController commandController = factory.createCommandController(Constants.BASE_COMMAND_CONTROLLER, utility, controller);
        controller.setCommandController(commandController);
        
        ISquareController squareController = factory.createSquareController(Constants.BASE_SQUARE_CONTROLLER, utility, controller, logger, keyPair);
        this.server = factory.createServer(Constants.BASE_SERVER, Integer.parseInt(port), squareController, logger, this);
        hidingServer = false;
        this.defaultName = utility.readFile(Constants.DEFAULT_NAME_FILE);
    }

    @Override
    public void run() {
        this.server.start();
    }

    
    public void stopIt() {
        if (server != null) {
            server.teardown();
        }
        utility.deleteFile("hub.exit");
    }

    public void close(int exitCode) {
        stopIt();
        if (exitCode != Constants.SYSTEM_EXIT_ALREADY_RUNNING) {
            utility.deleteFile(Constants.LOCK_FILE);
        }
    }

    public void closeApp(int exitCode, int shutdownCode) {
        logger.logInfo(Constants.CLOSING_LOG_MESSAGE);
        close(exitCode);
        systemExit.handleExit(shutdownCode);
    }

    public void hideServer() {
        hidingServer = true;
    }

    public boolean isHidingServer() {
        return hidingServer;
    }

    public void exposeServer() {
        hidingServer = false;
    }

    public int getLoggerType() {
        return loggerType;
    }

    public void sendPort(String port) {
        this.port = port;
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

    public void updateSquare(ISquare square) {
        String name = utility.concatStrings(square.getSafeLowerName(), Constants.SQUARE_FILE_EXT);
        utility.deleteFile(name);
        utility.writeFile(name, square.toString());
    }

    public String getPublicKeyBase64() {
        return keyPair.getPublicKeyBase64();
    }

    public String getDefaultName() {
        return this.defaultName;
    }

    public void sendDefaultName(String name) {
        this.defaultName = name;
    }

    public void start(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public IDialogController getDialogController() {
        return controller;
    }
}

import java.util.Arrays;

public class Hub {
    public static void main(String[] args) {
        String loggerFlag = Constants.EMPTY_STRING;
        Boolean debugFlag = false;
        if (args.length > 0) {
            loggerFlag = args[0];
            System.out.println(loggerFlag);
        } else {
            System.out.println("no args");
        }
        if (args.length > 1 && args[1].equals("-d")) {
                debugFlag = true;
        }
        IFactory factory = new Factory();
        ISystemExit systemExit = factory.createSystemExit(Constants.BASE_SYSTEM_EXIT);

        IApp app = factory.createApp(loggerFlag, factory.createAlertBox(Constants.BASE_ALERT_BOX),
                systemExit, factory);
        app.setDebug(debugFlag);
        systemExit.setParent(app);
        app.start();
        ILogIt errorLogger = factory.createLogger(Constants.ERROR_LOGGER, Constants.ERROR_LOG_FILE,
                factory.createUtility(Constants.BASE_UTILITY, app.getDialogController()), app.getDialogController());

        IUtility utility = factory.createUtility(Constants.BASE_UTILITY, app.getDialogController());
        while (!utility.checkFileExists(Constants.HUB_EXIT_FILE)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                errorLogger.logInfo(utility.concatStrings(ie.getMessage(), Constants.NEWLINE, Arrays.toString(ie.getStackTrace())));
                Thread.currentThread().interrupt();
            }
        }

        app.closeApp(Constants.GRACEFUL_SHUTDOWN, Constants.GRACEFUL_SHUTDOWN);
    }
}

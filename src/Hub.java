public class Hub {
    public static void main(String[] args) {
        String loggerFlag = Constants.EMPTY_STRING;
        if (args.length > 0) {
            loggerFlag = args[0];
            System.out.println(loggerFlag);
        } else {
            System.out.println("no args");
        }
        IFactory factory = new Factory();
        IApp app = factory.createApp(loggerFlag, factory.createAlertBox(Constants.BASE_ALERT_BOX),
                factory.createSystemExit(Constants.BASE_SYSTEM_EXIT), factory);
        app.start();

        IUtility utility = factory.createUtility(Constants.BASE_UTILITY);
        while (!utility.checkFileExists("hub.exit")) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        app.closeApp(Constants.GRACEFUL_SHUTDOWN, Constants.GRACEFUL_SHUTDOWN);
    }
}

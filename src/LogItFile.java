public class LogItFile implements ILogIt {
    private static LogItFile logItConsole;
    private IUtility utility;
    private String file;

    private LogItFile(IUtility utility, String file) {
        this.utility = utility;
        this.file = file;
        utility.deleteFile(file);
    }

    public static ILogIt create(IUtility utility, String file) {
        if (logItConsole == null) {
            logItConsole = new LogItFile(utility, file);
        }        

        return logItConsole;
    }

    public void logInfo(String msg) {
        if (!msg.startsWith(Constants.NEWLINE)) {
            msg = Constants.NEWLINE + msg;
        }
        utility.appendToFile(file, msg);
    }
}

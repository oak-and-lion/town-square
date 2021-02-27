public class LogItError implements ILogIt {
    private IDialogController dialogController;
    private IFileLogger fileLogger;
    private String file;
    private IUtility utility;

    private static final long MAX_ERROR_FILE_SIZE = 100000;

    public LogItError(IUtility utility, String file, IDialogController dialogController) {
        fileLogger = LogItFile.createFileLogger(utility, file, dialogController);
        this.dialogController = dialogController;
        this.file = file;
        this.utility = utility;
    }

    public void logInfo(String msg) {
        Boolean newFile = false;
        if (utility.getFileSize(file) > MAX_ERROR_FILE_SIZE) {
            newFile = true;
        }
        logInfo(msg, newFile);
        // update the ui in some way?
    }

    public void logInfo(String msg, Boolean newFile) {
        fileLogger.logInfo(msg, newFile);
    }

    public IDialogController getDialogController() {
        return dialogController;
    }
}
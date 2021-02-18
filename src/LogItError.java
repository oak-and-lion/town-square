public class LogItError implements ILogIt {
    private IDialogController dialogController;
    private ILogIt fileLogger;

    public LogItError(IUtility utility, String file, IDialogController dialogController) {
        fileLogger = LogItFile.create(utility, file, dialogController);
        this.dialogController = dialogController;
    }
    
    public void logInfo(String msg) {
        fileLogger.logInfo(msg);
        dialogController.showList(new String[] {msg}, "Error Encountered", "Error");
    }

    public IDialogController getDialogController() {
        return dialogController;
    }
}
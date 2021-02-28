public class LogItEmpty implements ILogIt {
    private static LogItEmpty console;
    private IDialogController dialogController;

    private LogItEmpty(IDialogController dialogController) {
        this.dialogController = dialogController;
    }

    public static ILogIt create(IDialogController dialogController) {
        if (console == null) {
            console = new LogItEmpty(dialogController);
        }

        return console;
    }

    public void logInfo(String msg) {
        // log no where
    }

    public IDialogController getDialogController() {
        return dialogController;
    }
}
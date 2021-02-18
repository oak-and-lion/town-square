public class LogItConsole implements ILogIt {
    private static LogItConsole console;

    private IDialogController dialogController;

    private LogItConsole(
        IDialogController dialogController
    ) {
        this.dialogController = dialogController;
    }

    public static ILogIt create(IDialogController dialogController) {
        if (console == null) {
            console = new LogItConsole(dialogController);
        }

        return console;
    }

    public void logInfo(String msg) {
        System.out.println(msg);
    }

    public IDialogController getDialogController() {
        return dialogController;
    }
}

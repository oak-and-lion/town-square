import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogItFile implements ILogIt {
    private IUtility utility;
    private String file;
    private IDialogController dialogController;

    private LogItFile(IUtility utility, String file, IDialogController dialogController) {
        this.utility = utility;
        this.file = file;
        this.dialogController = dialogController;
    }

    public static ILogIt create(IUtility utility, String file, IDialogController dialogController) {
        return new LogItFile(utility, file, dialogController);
    }

    public void logInfo(String msg) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        msg = utility.concatStrings(dtf.format(now), Constants.COLON, Constants.SPACE, msg);

        if (!msg.startsWith(Constants.NEWLINE)) {
            msg = utility.concatStrings(Constants.NEWLINE, msg);
        }

        utility.appendToFile(file, msg);
    }

    public IDialogController getDialogController() {
        return this.dialogController;
    }
}

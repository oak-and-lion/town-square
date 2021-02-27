import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogItFile implements ILogIt, IFileLogger {
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

    public static IFileLogger createFileLogger(IUtility utility, String file, IDialogController dialogController) {
        return new LogItFile(utility, file, dialogController);
    }

    public void logInfo(String msg) {
        logInfo(msg, false);
    }

    public void logInfo(String msg, Boolean newFile) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        msg = utility.concatStrings(dtf.format(now), Constants.COLON, Constants.SPACE, msg);

        if (!msg.startsWith(Constants.NEWLINE)) {
            msg = utility.concatStrings(Constants.NEWLINE, msg);
        }

        String tempFile = file;
        if (newFile) {
            DateTimeFormatter fdtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
            int index = file.indexOf(Constants.PERIOD);
            String ext = file.substring(index, file.length());
            tempFile = utility.concatStrings(file.substring(0, index), fdtf.format(now), ext);
        }

        utility.appendToFile(tempFile, msg);
    }

    public IDialogController getDialogController() {
        return this.dialogController;
    }
}

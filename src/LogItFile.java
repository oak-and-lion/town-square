import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogItFile implements ILogIt {
    private IUtility utility;
    private String file;

    private LogItFile(IUtility utility, String file) {
        this.utility = utility;
        this.file = file;
    }

    public static ILogIt create(IUtility utility, String file) {
        return new LogItFile(utility, file);
    }

    public void logInfo(String msg) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        msg = dtf.format(now) + Constants.COLON + Constants.SPACE + msg;

        if (!msg.startsWith(Constants.NEWLINE)) {
            msg = Constants.NEWLINE + msg;
        }

        utility.appendToFile(file, msg);
    }
}

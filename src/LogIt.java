import java.util.logging.Level;
import java.util.logging.Logger;

public class LogIt {
    private LogIt() {}

    public static void logInfo(String msg) {
        Logger.getGlobal().log(Level.INFO, msg);
    }
}

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogIt {
    public static void LogInfo(String msg) {
        Logger.getGlobal().log(Level.INFO, msg);
    }
}

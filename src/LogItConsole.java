import java.util.logging.Level;
import java.util.logging.Logger;

public class LogItConsole implements ILogIt {
    private static LogItConsole console;

    private LogItConsole() {}

    public static ILogIt create() {
        if (console == null) {
            console = new LogItConsole();
        }

        return console;
    }

    public void logInfo(String msg) {
        Logger.getGlobal().log(Level.INFO, msg);
    }
}

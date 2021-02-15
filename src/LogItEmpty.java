public class LogItEmpty implements ILogIt {
    private static LogItEmpty console;

    private LogItEmpty() {}

    public static ILogIt create() {
        if (console == null) {
            console = new LogItEmpty();
        }

        return console;
    }

    public void logInfo(String msg) {
        // log no where
    }
}
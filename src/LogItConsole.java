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
        System.out.println(msg);
    }
}

public class SystemExit implements ISystemExit {
    public void handleExit(int returnCode) {
        System.exit(returnCode);
    }
}

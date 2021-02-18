public class xxMockILogIt implements ILogIt {
    public void logInfo(String msg) {
        // not needed
    }
    public IDialogController getDialogController() {
        return new xxMockIDialogController();
    }
}

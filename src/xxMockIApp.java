import javafx.stage.Stage;

public class xxMockIApp implements IApp {
    public void close(int i) {
        // not needed
    }
    public void updateSquare(ISquare square) {
        // not needed
    }

    public String getPublicKeyBase64() {
        return "";
    }

    public void hideServer() {
        // not needed
    }

    public void exposeServer() {
        // not needed
    }

    public String getDefaultName() {
        return "mock";
    }

    public void sendPort(String port) {
        // not needed
    }

    public boolean isHidingServer() {
        return false;
    }

    public void sendIP(String s, String s2, String s3) {
        // not needed
    }

    public void closeApp(int i, int i2) {
        // not needed
    }

    public void sendDefaultName(String name) {
        // not needed
    }

    public Stage getStage() {
        return new Stage();
    }

    public int getLoggerType() {
        return Constants.FILE_LOGGER;
    }

    public IDialogController getDialogController() {
        return new xxMockIDialogController();
    }

    public void start(Stage stage) {
        // do nothing
    }

    public void start() {
        start(null);
    }
}

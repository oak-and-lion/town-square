import javafx.stage.Stage;

public interface IApp {
    void sendDefaultName(String defaultName);
    void sendPort(String port);
    void updateSquare(ISquare square);
    void close(int exitCode);
    String getDefaultName();
    String getPublicKeyBase64();
    void sendIP(String ip, String oldIp, String uniqueId);
    void closeApp(int exitCode, int shutdownCode);
    void hideServer();
    void exposeServer();
    boolean isHidingServer();
    Stage getStage();
    int getLoggerType();
    void start(Stage stage);
    void start();
}

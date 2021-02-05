public interface IApp {
    void sendDefaultName(String defaultName);
    void sendPort(String port);
    void updateSquare(Square square);
    void close(int exitCode);
    String getDefaultName();
    String getPublicKeyBase64();
    void sendIP(String ip, String oldIp, String uniqueId);
    void closeApp(int exitCode, int shutdownCode);
}

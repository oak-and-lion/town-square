public interface IApp {
    void sendDefaultName(String defaultName);
    void sendPort(String port);
    void updateSquare(Square square);
    void close();
    String getDefaultName();
    String getPublicKeyBase64();
    void sendAlias(String alias);
    void sendIP(String ip, String oldIp, String uniqueId);
}

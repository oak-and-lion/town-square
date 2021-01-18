public interface ISquareKeyPair {
    void setPrivateKeyFromBase64(String b64Key);
    void setPublicKeyFromBase64(String b64Key);
    String decryptFromBase64(String data);
    String getPublicKeyBase64();
    String getPrivateKeyBase64();
    String encryptToBase64(String value);
}

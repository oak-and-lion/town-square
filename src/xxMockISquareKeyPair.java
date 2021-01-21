public class xxMockISquareKeyPair implements ISquareKeyPair {
    public xxMockISquareKeyPair(IUtility utility) {
        
    }

    public void setPrivateKeyFromBase64(String b64Key) {
        // not needed 
    }
    public void setPublicKeyFromBase64(String b64Key) {
        // not needed
    }
    public String decryptFromBase64(String data) {
        return Constants.EMPTY_STRING;
    }
    public String getPublicKeyBase64() {
        return Constants.EMPTY_STRING;
    }
    public String getPrivateKeyBase64() {
        return Constants.EMPTY_STRING;
    }
    public String encryptToBase64(String value) {
        return Constants.EMPTY_STRING;
    }
}

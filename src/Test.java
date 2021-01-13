public class Test {
    public static void main(String[] args) {
        CryptoUtils cryptoUtils = new CryptoUtils();
        SquareKeyPair keys = cryptoUtils.generateKeyPair();

        String data = "test me";

        byte[] encrypt1 = keys.encrypt(data);
        String decrypt1 = keys.decrypt(encrypt1);

        SquareKeyPair compKeys = new SquareKeyPair();
        compKeys.setPrivateKeyFromBase64(keys.getPrivateKeyBase64());
        compKeys.setPublicKeyFromBase64(keys.getPublicKeyBase64());

        byte[] encrypt2 = compKeys.encrypt(data);
        String decrypt2 = compKeys.decrypt(encrypt1);

        String decrypt3 = compKeys.decrypt(encrypt2);

        LogIt.LogInfo(Boolean.toString(data.equals(decrypt1)));
        
        LogIt.LogInfo(Boolean.toString(decrypt3.equals(data)));

        LogIt.LogInfo(Boolean.toString(data.equals(decrypt2)));

        LogIt.LogInfo(Boolean.toString(decrypt1.equals(decrypt2)));
    }
}

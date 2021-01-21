public class xxMockIUtility implements IUtility {
    public String createUUID() {
        return Constants.EMPTY_STRING;
    }
    public FileWriteResponse writeFile(String file, String data) {
        return null;
    }
    public FileWriteResponse writeBinaryFile(String file, byte[] data) {
        return null;
    }
    public FileWriteResponse appendToFile(String file, String data) {
        return null;
    }
    public String readFile(String file) {
        return Constants.EMPTY_STRING;
    }
    public String readFile(String file, int afterLine) {
        return Constants.EMPTY_STRING;
    }
    public String readLastLineOfFile(String file) {
        return Constants.EMPTY_STRING;
    }
    public String[] searchFile(String file, String value, boolean startsWith) {
        return new String[0];
    }
    public String[] searchFile(String file, String value, boolean startsWith, int lastKnownRow) {
        return new String[0];
    }
    public String generateRandomString(int length) {
        return Constants.EMPTY_STRING;
    }
    public String encrypt(String data, String password) {
        return Constants.EMPTY_STRING;
    }
    public String decrypt(String data, String password) {
        return Constants.EMPTY_STRING;
    }
    public byte[] decryptToBinary(String data, String password) {
        return new byte[0];
    }
    public String[] getFiles(String match) {
        return new String[0];
    }
    public boolean checkFileExists(String file) {
         return true;
    }
    public int countLinesInFile(String file) {
        return 0;
    }
    public int findFirstOccurence(String file, String value, boolean startsWith, boolean notFoundReturnZero) {
        return 0;
    }
    public String createIdFile(String file) {
        return Constants.EMPTY_STRING;
    }
    public String getRemoteIP() {
        return Constants.EMPTY_STRING;
    }
    public IPAddress[] getLocalIPs() {
        return new IPAddress[0];
    }
    public boolean deleteFile(String file) {
        return true;
   }
    public boolean deleteFiles(String match) {
        return true;
   }
    public String convertToBase64(byte[] bytes) {
        return Constants.EMPTY_STRING;
    }
    public byte[] convertFromBase64(String data) {
        return new byte[0];
    }
}

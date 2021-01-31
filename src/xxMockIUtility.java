public class xxMockIUtility implements IUtility {
    private static final String SEARCH_VALUE = "searchFound";

    public String createUUID() {
        return Constants.EMPTY_STRING;
    }
    public FileWriteResponse writeFile(String file, String data) {
        return new FileWriteResponse(true, 1);
    }
    public FileWriteResponse writeBinaryFile(String file, byte[] data) {
        return new FileWriteResponse(true, 1);
    }
    public FileWriteResponse appendToFile(String file, String data) {
        return new FileWriteResponse(true, 1);
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
        return searchFile(file, value, startsWith, -1);
    }
    public String[] searchFile(String file, String value, boolean startsWith, int lastKnownRow) {
        final String SEARCH_FOUND_USER = "searchFound~_~one~_~two~_~three~_~four";;
        if (value.equals(SEARCH_VALUE)) {
            return new String[] {SEARCH_FOUND_USER};
        }

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
    public String getRemoteIP(ILogIt logger) {
        return Constants.EMPTY_STRING;
    }
    public IPAddress[] getLocalIPs(ILogIt logger) {
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

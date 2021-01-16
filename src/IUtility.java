public interface IUtility {
    String createUUID();
    FileWriteResponse writeFile(String file, String data);
    FileWriteResponse appendToFile(String file, String data);
    String readFile(String file);
    String readFile(String file, int afterLine);
    String readLastLineOfFile(String file);
    String[] searchFile(String file, String value, boolean startsWith);
    String[] searchFile(String file, String value, boolean startsWith, int lastKnownRow);
    String generateRandomString(int length);
    String encrypt(String data, String password);
    String decrypt(String data, String password);
    String[] getFiles(String match);
    boolean checkFileExists(String file);
    int countLinesInFile(String file);
    int findFirstOccurence(String file, String value, boolean startsWith, boolean notFoundReturnZero);
    String createIdFile(String file);
    String getRemoteIP();
    IPAddress[] getLocalIPs();
    boolean deleteFile(String file);
}

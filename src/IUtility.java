import java.util.zip.ZipOutputStream;

public interface IUtility {
    String createUUID();
    FileWriteResponse writeFile(String file, String data);
    FileWriteResponse writeBinaryFile(String file, byte[] data);
    FileWriteResponse appendToFile(String file, String data);
    byte[] readBinaryFile(String file);
    String readFile(String file);
    String readFile(String file, int afterLine);
    String readLastLineOfFile(String file);
    String[] searchFile(String file, String value, boolean startsWith);
    String[] searchFile(String file, String value, boolean startsWith, int lastKnownRow);
    String generateRandomString(int length);
    String encrypt(String data, String password);
    String decrypt(String data, String password);
    byte[] decryptToBinary(String data, String password);
    String[] getFiles(String match);
    boolean checkFileExists(String file);
    int countLinesInFile(String file);
    int findFirstOccurence(String file, String value, boolean startsWith, boolean notFoundReturnZero);
    String createIdFile(String file);
    String getRemoteIP(ILogIt logger);
    IPAddress[] getLocalIPs(ILogIt logger);
    boolean deleteFile(String file);
    boolean deleteFiles(String match);
    String convertToBase64(byte[] bytes);
    byte[] convertFromBase64(String data);
    void addToZip(String srcFile, ZipOutputStream zipOut);
}

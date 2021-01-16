import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.Base64;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.net.*;
import java.io.*;

public class Utility {
    private static Utility utility;
    private static final String USER_DIR = "user.dir";
    private static final String PATH_DELIMITER = "/";
    private static final String REMOTE_IP_URL = "https://checkip.amazonaws.com/";
    private static final int NOT_FOUND_ROW = -1;
    private static final String EMPTY_STRING = "";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    private Random random;

    private Utility() {
        random = new Random();
    }

    public static Utility create() {
        if (utility == null) {
            utility = new Utility();
        }

        return utility;
    }

    public static Utility forceNew() {
        return new Utility();
    }

    public String createUUID() {
        return UUID.randomUUID().toString();
    }

    public String getRemoteIP() {
        String result = "";
        try {
            URL whatismyip = new URL(REMOTE_IP_URL);

            BufferedReader in = openReader(whatismyip);
            if (in != null) {
                result = bufferedReaderRead(in);
            }
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        }

        return result;
    }

    public IPAddress[] getLocalIPs() {
        ArrayList<IPAddress> result = new ArrayList<IPAddress>();

        Enumeration<NetworkInterface> n;
        try {
            n = NetworkInterface.getNetworkInterfaces();

            while (n.hasMoreElements()) {
                NetworkInterface e = n.nextElement();

                Enumeration<InetAddress> a = e.getInetAddresses();
                while (a.hasMoreElements()) {
                    InetAddress addr = a.nextElement();
                    result.add(new IPAddress(addr.getHostAddress(), addr.getHostAddress()));
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }

        return result.toArray(new IPAddress[result.size()]);
    }

    public String[] getFiles(String match) {
        String finalString = match.toLowerCase().trim();
        // Creating a File object for directory
        File directoryPath = new File(System.getProperty(USER_DIR) + PATH_DELIMITER);
        FilenameFilter textFilefilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                return (lowercaseName.contains(finalString));
            }
        };
        // List of all the text files
        return directoryPath.list(textFilefilter);
    }

    private String bufferedReaderRead(BufferedReader in) {
        String result = "";
        try {
            result = in.readLine(); // you get the IP as a String
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return result;
    }

    private BufferedReader openReader(URL url) {
        BufferedReader in = null;

        try {
            in = new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return in;
    }

    public boolean checkFileExists(String file) {
        String filepath = getFilePath(file);
        File f = new File(filepath);

        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }

        return f.exists();
    }

    public String createIdFile(String file) {
        String uuid = createUUID();

        writeFile(file, uuid);

        return uuid;
    }

    public boolean deleteFile(String file) {
        boolean result = true;
        if (checkFileExists(file)) {
            File f = new File(getFilePath(file));
            try {
                Files.delete(f.toPath());
            } catch (IOException se) {
                se.printStackTrace();
                result = false;
            }
        }

        return result;
    }

    public FileWriteResponse writeFile(String file, String data) {
        FileWriteResponse result = new FileWriteResponse(false, 0);
        File f = new File(getFilePath(file));

        try {
            if (!checkFileExists(file)) {
                boolean b = f.createNewFile();

                if (b) {
                    writeToFile(file, data);
                }
            } else {
                writeToFile(file, data);
            }
            Charset charset = StandardCharsets.UTF_8;
            String path = System.getProperty(USER_DIR) + PATH_DELIMITER + file;
            List<String> s = Files.readAllLines(Paths.get(path), charset);
            result.setLineCount(s.size());
            result.setSuccessful(true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SecurityException se) {
            se.printStackTrace();
        }

        return result;
    }

    public FileWriteResponse appendToFile(String file, String data) {
        FileWriteResponse result = new FileWriteResponse(false, 0);
        String path = System.getProperty(USER_DIR) + PATH_DELIMITER + file;

        try {
            Charset charset = StandardCharsets.UTF_8;
            Files.writeString(Paths.get(path), data, StandardOpenOption.APPEND);
            List<String> s = Files.readAllLines(Paths.get(path), charset);
            result.setLineCount(s.size());
            result.setSuccessful(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void writeToFile(String f, String data) {
        try (FileWriter fw = new FileWriter(getFilePath(f))) {
            bufferedWrite(fw, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bufferedWrite(FileWriter fw, String data) {
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public String readFile(String file) {
        return readFile(file, -1);
    }

    public String readFile(String file, int lastKnownRow) {
        if (!checkFileExists(file)) {
            return EMPTY_STRING;
        }

        StringBuilder result = new StringBuilder();

        // using class of nio file package
        Path filePath = Paths.get(file);

        // converting to UTF 8
        Charset charset = StandardCharsets.UTF_8;

        int count = 0;
        // try with resource
        try (BufferedReader bufferedReader = Files.newBufferedReader(filePath, charset)) {
            String line;
            boolean first = true;
            while ((line = bufferedReader.readLine()) != null) {
                if (count > lastKnownRow) {
                    if (!first) {
                        result.append("%%%");
                    }
                    result.append(line);
                    first = false;
                }
                count++;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return result.toString();
    }

    public String[] searchFile(String file, String value, boolean startsWith) {
        return searchFile(file, value, startsWith, -1);
    }

    public String[] searchFile(String file, String value, boolean startsWith, int lastKnownRow) {
        if (!checkFileExists(file)) {
            return new String[0];
        }
        ArrayList<String> temp = new ArrayList<String>();

        // using class of nio file package
        Path filePath = Paths.get(file);

        // converting to UTF 8
        Charset charset = StandardCharsets.UTF_8;

        int count = 0;

        // try with resource
        try (BufferedReader bufferedReader = Files.newBufferedReader(filePath, charset)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (count > lastKnownRow) {
                    String s = checkMatch(startsWith, value, line);
                    if (!s.equals(EMPTY_STRING)) {
                        temp.add(s);
                    }
                }

                count++;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return temp.toArray(new String[temp.size()]);
    }

    private String checkMatch(boolean startsWith, String value, String line) {
        if (startsWith) {
            if (line.startsWith(value)) {
                return line;
            }
        } else {
            if (line.contains(value)) {
                return line;
            }
        }

        return EMPTY_STRING;
    }

    public int countLinesInFile(String file) {
        int result = 0;
        // using class of nio file package
        Path filePath = Paths.get(file);

        // converting to UTF 8
        Charset charset = StandardCharsets.UTF_8;

        // try with resource
        try (BufferedReader bufferedReader = Files.newBufferedReader(filePath, charset)) {
            while ((bufferedReader.readLine()) != null) {
                result++;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return result;
    }

    public String readLastLineOfFile(String file) {
        if (!checkFileExists(file)) {
            return EMPTY_STRING;
        }

        String result = EMPTY_STRING;

        // using class of nio file package
        Path filePath = Paths.get(file);

        // converting to UTF 8
        Charset charset = StandardCharsets.UTF_8;

        // try with resource
        try (BufferedReader bufferedReader = Files.newBufferedReader(filePath, charset)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result = line;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return result;
    }

    public int findFirstOccurence(String file, String value, boolean startsWith, boolean notFoundReturnZero) {
        if (!checkFileExists(file)) {
            return NOT_FOUND_ROW;
        }
        int result = NOT_FOUND_ROW;

        // using class of nio file package
        Path filePath = Paths.get(file);

        // converting to UTF 8
        Charset charset = StandardCharsets.UTF_8;

        int count = 0;
        boolean found = false;
        // try with resource
        try (BufferedReader bufferedReader = Files.newBufferedReader(filePath, charset)) {
            String line;
            while ((line = bufferedReader.readLine()) != null && !found) {
                if (startsWith) {
                    if (line.startsWith(value)) {
                        result = count;
                        found = true;
                    }
                } else {
                    if (line.contains(value)) {
                        result = count;
                        found = true;
                    }
                }
                count++;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (!notFoundReturnZero && !found) {
            result = count;
        }

        return result;
    }

    public String decrypt(String data, String password) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKey secretKey = new SecretKeySpec(password.getBytes(), ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] clearMessage = cipher.doFinal(convertFromBase64(data));
            return new String(clearMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String encrypt(String data, String password) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKey secretKey = new SecretKeySpec(password.getBytes(), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedMessage = cipher.doFinal(data.getBytes());
            return convertToBase64(encryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String convertToBase64(byte[] bytes) {
        byte[] encodedBytes = Base64.getEncoder().encode(bytes);
        return new String(encodedBytes);
    }

    public byte[] convertFromBase64(String data) {
        return Base64.getDecoder().decode(data);
    }

    public String generateRandomString(int targetStringLength) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'

        return random.ints(leftLimit, rightLimit + 1).filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }

    private String getFilePath(String file) {
        return System.getProperty(USER_DIR) + PATH_DELIMITER + file;
    }
}
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.util.Base64;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.net.*;
import java.io.*;

public class Utility implements IUtility {
    private static Utility utility;

    private Random random;

    private Utility() {
        random = new Random();
    }

    public static IUtility create() {
        if (utility == null) {
            utility = new Utility();
        }

        return utility;
    }

    public static IUtility forceNew() {
        return new Utility();
    }

    public String createUUID() {
        return UUID.randomUUID().toString();
    }

    public String getRemoteIP(ILogIt logger) {
        String result = Constants.EMPTY_STRING;
        try {
            URL whatismyip = new URL(Constants.REMOTE_IP_URL);

            BufferedReader in = openReader(whatismyip);
            if (in != null) {
                result = bufferedReaderRead(in);
            }
        } catch (MalformedURLException mue) {
            logger.logInfo(mue.getMessage());
        }

        return result.replace(Constants.HELLO_WORLD, Constants.EMPTY_STRING);
    }

    public IPAddress[] getLocalIPs(ILogIt logger) {
        ArrayList<IPAddress> result = new ArrayList<>();

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
            logger.logInfo(e1.getMessage());
        }

        return result.toArray(new IPAddress[result.size()]);
    }

    public String[] getFiles(String match) {
        String finalString = match.toLowerCase().trim();
        // Creating a File object for directory
        File directoryPath = new File(concatStrings(System.getProperty(Constants.USER_DIR), Constants.PATH_DELIMITER));
        FilenameFilter textFilefilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                return (lowercaseName.contains(finalString));
            }
        };
        // List of all the text files
        return directoryPath.list(textFilefilter);
    }

    public boolean deleteFiles(String match) {
        String[] files = getFiles(match);

        for (String file : files) {
            deleteFile(file);
        }

        return true;
    }

    private String bufferedReaderRead(BufferedReader in) {
        String result = Constants.EMPTY_STRING;
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

    public FileWriteResponse writeBinaryFile(String file, byte[] data) {
        FileWriteResponse result = new FileWriteResponse(false, 0);
        try (OutputStream outputStream = new FileOutputStream(file)) {
            int byteRead = 0;

            for (int x = 0; x < data.length; x++) {
                outputStream.write(data[x]);
                byteRead++;
            }
            result.setLineCount(byteRead);
            result.setSuccessful(true);
        } catch (IOException ioe) {
            ioe.printStackTrace();
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
            String path = concatStrings(System.getProperty(Constants.USER_DIR), Constants.PATH_DELIMITER, file);
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
        if (!checkFileExists(file)) {
            result = writeFile(file, data);
        } else {
            String path = concatStrings(System.getProperty(Constants.USER_DIR), Constants.PATH_DELIMITER, file);

            try {
                Charset charset = StandardCharsets.UTF_8;
                Files.writeString(Paths.get(path), data, StandardOpenOption.APPEND);
                List<String> s = Files.readAllLines(Paths.get(path), charset);
                result.setLineCount(s.size());
                result.setSuccessful(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public byte[] readBinaryFile(String file) {
        if (!checkFileExists(file)) {
            return new byte[0];
        }

        Path path = Paths.get(file);
        try {
            return Files.readAllBytes(path);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (OutOfMemoryError oome) {
            oome.printStackTrace();
        } catch (SecurityException se) {
            se.printStackTrace();
        }

        return new byte[0];
    }

    public String readFile(String file) {
        return readFile(file, -1);
    }

    public String readFile(String file, int lastKnownRow) {
        if (!checkFileExists(file)) {
            return Constants.EMPTY_STRING;
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
                        result.append(Constants.READ_FILE_DATA_SEPARATOR);
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
        ArrayList<String> temp = new ArrayList<>();

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
                    if (!s.equals(Constants.EMPTY_STRING)) {
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

        return Constants.EMPTY_STRING;
    }

    public int countLinesInFile(String file) {
        if (!checkFileExists(file)) {
            return Constants.NOT_FOUND_ROW;
        }
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
            return Constants.EMPTY_STRING;
        }

        String result = Constants.EMPTY_STRING;

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
        int result = Constants.NOT_FOUND_ROW;

        if (!checkFileExists(file)) {
            return result;
        }

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
        return new String(decryptToBinary(data, password));
    }

    public byte[] decryptToBinary(String data, String password) {
        try {
            Cipher cipher = Cipher.getInstance(Constants.TRANSFORMATION);
            SecretKey secretKey = new SecretKeySpec(password.getBytes(), Constants.ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(convertFromBase64(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public String encrypt(String data, String password) {
        try {
            Cipher cipher = Cipher.getInstance(Constants.TRANSFORMATION);
            SecretKey secretKey = new SecretKeySpec(password.getBytes(), Constants.ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedMessage = cipher.doFinal(data.getBytes());
            return convertToBase64(encryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Constants.EMPTY_STRING;
    }

    public String convertToBase64(byte[] bytes) {
        byte[] encodedBytes = Base64.getEncoder().encode(bytes);
        return new String(encodedBytes);
    }

    public byte[] convertFromBase64(String data) {
        if (data == null) {
            return new byte[0];
        }
        try {
            return Base64.getDecoder().decode(data);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    public String generateRandomString(int targetStringLength) {
        int leftLimit = Constants.ASCII_ZERO; // numeral '0'
        int rightLimit = Constants.ASCII_LOWER_Z; // letter 'z'

        return random.ints(leftLimit, add(rightLimit, 1)).filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }

    private String getFilePath(String file) {
        return concatStrings(System.getProperty(Constants.USER_DIR), Constants.PATH_DELIMITER, file);
    }

    public void addToZip(String srcFile, ZipOutputStream zipOut) {
        File fileToZip = new File(srcFile);
        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean unzip(String fileZip) {
        File destDir = new File(Constants.EMPTY_STRING);

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = new File(destDir, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException(concatStrings("Failed to create directory ", newFile.toString()));
                    }
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException(concatStrings("Failed to create directory ", parent.toString()));
                    }

                    // write file content
                    writeZipEntry(newFile, zis);
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private void writeZipEntry(File newFile, ZipInputStream zis) {
        try (FileOutputStream fos = new FileOutputStream(newFile)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public String concatStrings(String... strings) {
        StringBuilder result = new StringBuilder();

        for (String string : strings) {
            result.append(string);
        }

        return result.toString();
    }

    public int add(int... ints) {
        int result = 0;

        for (int i : ints) {
            result += i;
        }

        return result;
    }

    public String memberEncrypt(IFactory factory, String data, String memberPublicKey) {
        ISquareKeyPair tempKeys = factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, this);
        
        tempKeys.setPublicKeyFromBase64(memberPublicKey);

        return tempKeys.encryptToBase64(data);
    }
}
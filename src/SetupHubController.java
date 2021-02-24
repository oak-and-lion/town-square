import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SetupHubController implements ISetup {
    private IUtility utility;
    private IFactory factory;
    private IApp app;

    public SetupHubController(IUtility utility, IFactory factory, IApp app) {
        this.utility = utility;
        this.factory = factory;
        this.app = app;
    }

    public void runSetup() {
        String remoteIP = utility.getRemoteIP();
        IPAddress[] localIPs = utility.getLocalIPs();
        ArrayList<DoubleString> ips = new ArrayList<>();
        ips.add(new DoubleString("1", remoteIP));
        StringBuilder ipsPrompt = new StringBuilder();
        ipsPrompt.append("Enter ip, or: ");
        ipsPrompt.append(Constants.NEWLINE);
        ipsPrompt.append(utility.concatStrings("1 for ", remoteIP, Constants.NEWLINE));
        int count = 2;
        for (IPAddress address : localIPs) {
            ips.add(new DoubleString(Integer.toString(count), address.getDisplay()));
            ipsPrompt.append(
                    utility.concatStrings(Integer.toString(count), " for ", address.getDisplay(), Constants.NEWLINE));
            count++;
        }

        ipsPrompt.append(utility.concatStrings(Constants.COLON, Constants.SPACE));
        ISquareKeyPair keys = factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility);
        ICryptoUtils cryptoUtils = factory.createCryptoUtils(Constants.BASE_CRYPTO_UTILS, app.getDialogController());

        if (utility.checkFileExists(Constants.PUBLIC_KEY_FILE) && utility.checkFileExists(Constants.PRIVATE_KEY_FILE)) {
            keys.setPublicKeyFromBase64(utility.readFile(Constants.PUBLIC_KEY_FILE));
            keys.setPrivateKeyFromBase64(utility.readFile(Constants.PRIVATE_KEY_FILE));
        } else {
            keys = cryptoUtils.generateKeyPair();
            utility.writeFile(Constants.PUBLIC_KEY_FILE, keys.getPublicKeyBase64());
            utility.writeFile(Constants.PRIVATE_KEY_FILE, keys.getPrivateKeyBase64());
        }

        processSetupFile(Constants.PORT_FILE, "Enter port, or X for default (44123): ",
                new DoubleString[] { new DoubleString("x", Constants.DEFAULT_PORT) });
        app.getDialogController().setPort(utility.readFile(Constants.PORT_FILE));
        
                processSetupFile(Constants.IP_FILE, ipsPrompt.toString(), ips.toArray(new DoubleString[ips.size()]));
        app.getDialogController().setRemoteIP(null, utility.readFile(Constants.IP_FILE));
        processSetupFile(Constants.DEFAULT_NAME_FILE, "Enter name, or X for default (Anonymous User): ",
                        new DoubleString[] { new DoubleString("x", Constants.DEFAULT_PORT) });
        if (!utility.checkFileExists(Constants.UNIQUE_ID_FILE)) {
            writeData(Constants.UNIQUE_ID_FILE, utility.createUUID()).isSuccessful();
        }
        app.getDialogController().setUniqueId(utility.readFile(Constants.UNIQUE_ID_FILE));
        if (!utility.checkFileExists(Constants.DEFAULT_SQUARE_FILE)) {
            String defaultSquareInfo = utility.concatStrings(Constants.DEFAULT_SQUARE_NAME, Constants.COMMA,
                    utility.createUUID(), Constants.COMMA, Constants.DEFAULT_SQUARE_TAB_NAME, Constants.COMMA,
                    Constants.NOT_PRIVATE, Constants.COMMA, Constants.NO_PASSWORD_VALUE);
            utility.writeFile(Constants.DEFAULT_SQUARE_FILE, defaultSquareInfo);
        }
        if (!utility.checkFileExists(Constants.DEFAULT_SQUARE_ME_FILE)) {
            utility.writeFile(Constants.DEFAULT_SQUARE_ME_FILE,
                    utility.concatStrings(utility.readFile(Constants.DEFAULT_NAME_FILE), Constants.FILE_DATA_SEPARATOR,
                            keys.getPublicKeyBase64(), Constants.FILE_DATA_SEPARATOR,
                            utility.readFile(Constants.IP_FILE), Constants.FILE_DATA_SEPARATOR,
                            utility.readFile(Constants.PORT_FILE), Constants.FILE_DATA_SEPARATOR,
                            utility.readFile(Constants.UNIQUE_ID_FILE)));
        }
    }

    public BooleanString setupDNAPassword(String file) {
        return processSetupFile(file, "Enter clone password: ", new DoubleString[0]);
    }

    private BooleanString processSetupFile(String file, String prompt, DoubleString[] defaultValues) {
        BooleanString result = new BooleanString(true, Constants.EMPTY_STRING);
        if (!utility.checkFileExists(file)) {
            String input = getKeyboardInput(prompt);

            if (input != null) {
                String temp = input.trim().toLowerCase();
                for (DoubleString doubleString : defaultValues) {
                    if (temp.equals(doubleString.getStringOne())) {
                        input = doubleString.getStringTwo();
                        break;
                    }
                }
                if (file != null) {
                    FileWriteResponse response = writeData(file, input);
                    result.setBoolean(response.isSuccessful());
                }
                result.setString(input);
            } else {
                result.setBoolean(false);
            }
        }

        return result;
    }

    private FileWriteResponse writeData(String file, String data) {
        return utility.writeFile(file, data);
    }

    private String getKeyboardInput(String prompt) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print(prompt);
            return br.readLine();
        } catch (IOException ioe) {
            utility.logError(ioe.getMessage());
        }

        return null;
    }
}

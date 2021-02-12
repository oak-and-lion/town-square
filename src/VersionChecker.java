import java.util.ArrayList;
import java.util.Collections;

public class VersionChecker extends Thread implements IVersionChecker {
    private IUtility utility;
    private String uniqueId;
    private IFactory factory;
    private String port;
    private String ip;
    private boolean done;

    public VersionChecker(IUtility utility, String uniqueId, IFactory factory) {
        this.utility = utility;
        this.uniqueId = uniqueId;
        this.factory = factory;
        this.port = utility.readFile(Constants.PORT_FILE);
        this.ip = utility.readFile(Constants.IP_FILE);
        done = false;
    }

    @Override
    public void run() {
        checkVersion();
        done = true;
    }

    public boolean isDone() {
        return done;
    }

    public void checkVersion() {
        DoubleString[] allMembers = getAllMembers();
        for (DoubleString member : allMembers) {
            String[] info = member.getStringTwo().split(Constants.FILE_DATA_SEPARATOR);
            if (!(info[4].equals(uniqueId) && (info[2].equals(ip) && info[3].equals(port)))
                    && !info[0].startsWith(Constants.STAR)) {
                checkVersionAgainstMember(member.getStringTwo(), member.getStringOne());
            }
        }
    }

    private DoubleString[] getAllMembers() {
        ArrayList<DoubleString> members = new ArrayList<>();

        String[] squareFiles = utility.getFiles(Constants.SQUARE_FILE_EXT);

        for (String file : squareFiles) {

            String[] squareInfo = utility.readFile(file).split(Constants.COMMA);
            String invite = squareInfo[1];
            ArrayList<String> smembers = getMembersFromFile(
                    file.replace(Constants.SQUARE_FILE_EXT, Constants.MEMBERS_FILE_EXT));
            ArrayList<String> aliases = getMembersFromFile(
                    file.replace(Constants.SQUARE_FILE_EXT, Constants.ALIAS_FILE_EXT));
            ArrayList<DoubleString> newMembers = new ArrayList<>();
            for (String member : smembers) {
                members.add(new DoubleString(invite, member));
                String[] info = member.split(Constants.DATA_SEPARATOR);
                for (String alias : aliases) {
                    if (alias.startsWith(info[4])) {
                        AliasObject ao = new AliasObject(alias, info[1]);
                        for (int x = 0; x < ao.length(); x++) {
                            String temp = utility.concatStrings(Constants.NULL_TEXT, Constants.DATA_SEPARATOR, info[1],
                                    Constants.DATA_SEPARATOR, ao.getIp(x), Constants.DATA_SEPARATOR, ao.getPort(x),
                                    Constants.DATA_SEPARATOR, ao.getUniqueId());
                            newMembers.add(new DoubleString(invite, temp));
                        }
                    }
                }
            }

            members.addAll(newMembers);
        }

        return members.toArray(new DoubleString[members.size()]);
    }

    private ArrayList<String> getMembersFromFile(String file) {
        ArrayList<String> result = new ArrayList<>();

        String[] members = utility.readFile(file).split(Constants.READ_FILE_DATA_SEPARATOR);

        Collections.addAll(result, members);

        return result;
    }

    private void checkVersionAgainstMember(String member, String squareInvite) {
        String password = utility.generateRandomString(Constants.ENCRYPTION_KEY_LENGTH);
        String[] memberInfo = member.split(Constants.DATA_SEPARATOR);

        IClient client = factory.createClient(Constants.BASE_CLIENT, memberInfo[2], Integer.valueOf(memberInfo[3]),
                squareInvite);

        String encryptedPassword = utility.memberEncrypt(factory, password, memberInfo[1]);
        String encryptedData = utility.concatStrings(encryptedPassword, Constants.COMMAND_DATA_SEPARATOR,
                utility.encrypt(Constants.CHECK_VERSION_COMMAND, password));
        String result = client.sendMessage(encryptedData, Constants.ENCRYPT_CLIENT_TRANSFER);

        if (result.equals(Constants.EMPTY_STRING)) {
            return;
        }

        SquareResponse versionResponse = new SquareResponse(result);

        if (versionResponse.getMessage().equals(Constants.EMPTY_STRING)) {
            return;
        }

        String[] resultVersion = versionResponse.getMessage().split(Constants.PERIOD_SPLIT);

        String[] currentVersion = getKnownVersion().split(Constants.PERIOD_SPLIT);

        if (resultVersion.length == currentVersion.length && resultVersion.length == 3
                && !isVersionEqual(resultVersion, currentVersion)) {

            String encrypted = utility.encrypt(utility.concatStrings(Constants.GET_APP_JAR_COMMAND,
                    Constants.COMMAND_DATA_SEPARATOR, Constants.JAR_FILE, Constants.COMMAND_DATA_SEPARATOR, uniqueId),
                    password);
            String fin = utility.concatStrings(encryptedPassword, Constants.COMMAND_DATA_SEPARATOR, encrypted);
            String response = client.sendMessage(fin, Constants.ENCRYPT_CLIENT_TRANSFER);

            SquareResponse responseData = new SquareResponse(response);

            if (!responseData.getCode().equals(Constants.OK_RESULT)) {
                return;
            }

            byte[] data = utility.convertFromBase64(responseData.getMessage());

            utility.writeBinaryFile(Constants.TEMP_JAR_FILE, data);

            utility.writeFile(Constants.NEW_APP_VER_FILE, versionResponse.getMessage());
        }
    }

    private boolean isVersionEqual(String[] resultVersion, String[] currentVersion) {
        boolean result = true;

        for (int x = 0; x < resultVersion.length; x++) {
            int r = Integer.parseInt(resultVersion[x]);
            int c = Integer.parseInt(currentVersion[x]);

            if (r > c) {
                result = false;
                break;
            }
        }

        return result;
    }

    private String getKnownVersion() {
        if (utility.checkFileExists(Constants.NEW_APP_VER_FILE)) {
            return utility.readFile(Constants.NEW_APP_VER_FILE);
        }

        return Constants.VERSION;
    }
}

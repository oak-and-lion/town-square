import java.util.ArrayList;
import java.util.Collections;

public class VersionChecker extends Thread implements IVersionChecker {
    private IUtility utility;
    private String uniqueId;
    private IFactory factory;

    public VersionChecker(IUtility utility, String uniqueId, IFactory factory) {
        this.utility = utility;
        this.uniqueId = uniqueId;
        this.factory = factory;
    }

    @Override
    public void run() {
        checkVersion();
    }

    public void checkVersion() {
        DoubleString[] allMembers = getAllMembers();
        for (DoubleString member : allMembers) {
            String[] info = member.getStringTwo().split(Constants.FILE_DATA_SEPARATOR);
            if (!info[4].equals(uniqueId) && !info[0].startsWith(Constants.STAR)) {
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
            for (String member : smembers) {
                members.add(new DoubleString(invite, member));
            }
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
        String encryptedData = utility.concatStrings(encryptedPassword, Constants.COMMAND_DATA_SEPARATOR, utility.encrypt(Constants.CHECK_VERSION_COMMAND, password));
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
            
            String encrypted = utility.encrypt(utility.concatStrings(Constants.GET_APP_JAR_COMMAND, Constants.COMMAND_DATA_SEPARATOR
            , Constants.JAR_FILE, Constants.COMMAND_DATA_SEPARATOR, uniqueId), password);
            String response = client.sendMessage(encrypted, Constants.DO_NOT_ENCRYPT_CLIENT_TRANSFER);

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

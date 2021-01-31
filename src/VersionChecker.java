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
        String[] memberInfo = member.split(Constants.DATA_SEPARATOR);

        IClient client = factory.createClient(Constants.BASE_CLIENT, memberInfo[2], Integer.valueOf(memberInfo[3]),
                squareInvite);

        String result = client.sendMessage(Constants.CHECK_VERSION_COMMAND, false);

        if (result.equals(Constants.EMPTY_STRING)) {
            return;
        }

        SquareResponse versionResponse = new SquareResponse(result);

        if (versionResponse.getMessage().equals(Constants.EMPTY_STRING)) {
            return;
        }

        String[] resultVersion = versionResponse.getMessage().split(Constants.PERIOD_SPLIT);

        String[] currentVersion = Constants.VERSION.split(Constants.PERIOD_SPLIT);

        if (resultVersion.length == currentVersion.length && resultVersion.length == 3
                && !isVersionEqual(resultVersion, currentVersion)) {
            String response = client.sendMessage(Constants.REQUEST_FILE_COMMAND + Constants.COMMAND_DATA_SEPARATOR
                    + Constants.JAR_FILE + Constants.COMMAND_DATA_SEPARATOR + uniqueId, false);

            SquareResponse responseData = new SquareResponse(response);

            if (responseData.getMessage().equals(Constants.EMPTY_STRING)) {
                return;
            }

            ISquareKeyPair keys = factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility);
            keys.setPrivateKeyFromBase64(utility.readFile(Constants.PRIVATE_KEY_FILE));

            String[] fileData = responseData.getMessage().split(Constants.COMMAND_DATA_SEPARATOR);

            String key = keys.decryptFromBase64(fileData[0]);

            String f = utility.decrypt(fileData[1], key);

            byte[] imageFile = utility.convertFromBase64(f);

            utility.writeBinaryFile(Constants.TEMP_JAR_FILE, imageFile);
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
}

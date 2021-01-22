import java.util.ArrayList;
import java.util.Collections;

public class VersionChecker extends Thread implements IVersionChecker {
    private IUtility utility;
    private String uniqueId;

    public VersionChecker(IUtility utility, String uniqueId) {
        this.utility = utility;
        this.uniqueId = uniqueId;
    }

    @Override
    public void run() {
        checkVersion();
    }

    public void checkVersion() {
        String[] allMembers = getAllMembers();
        for (String member : allMembers) {
            String[] info = member.split(Constants.FILE_DATA_SEPARATOR);
            if (!info[4].equals(uniqueId)) {
                checkVersionAgainstMember(member);
            }
        }
    }

    private String[] getAllMembers() {
        ArrayList<String> members = new ArrayList<String>();

        String[] memberFiles = utility.getFiles(Constants.MEMBERS_FILE_EXT);

        for (String file : memberFiles) {
            members.addAll(getMembersFromFile(file));
        }

        return members.toArray(new String[members.size()]);
    }

    private ArrayList<String> getMembersFromFile(String file) {
        ArrayList<String> result = new ArrayList<String>();

        String[] members = utility.readFile(file).split(Constants.READ_FILE_DATA_SEPARATOR);

        Collections.addAll(result, members);

        return result;
    }

    private void checkVersionAgainstMember(String member) {
        String[] memberInfo = member.split(Constants.DATA_SEPARATOR);

        IClient client = Factory.createClient(Constants.BASE_CLIENT, memberInfo[2], Integer.valueOf(memberInfo[3]),
                Constants.MAIN);

        String result = client.sendMessage(Constants.VERSION, false);

        String[] resultVersion = result.split(Constants.PERIOD);

        String[] currentVersion = Constants.VERSION.split(Constants.PERIOD);

        if (resultVersion.length == currentVersion.length && resultVersion.length == 3
                && !isVersionEqual(resultVersion, currentVersion)) {
            String response = client.sendMessage(Constants.REQUEST_FILE_COMMAND + Constants.COMMAND_DATA_SEPARATOR
                    + Constants.JAR_FILE + Constants.COMMAND_DATA_SEPARATOR + uniqueId, false);

            SquareResponse responseData = new SquareResponse(response);

            ISquareKeyPair keys = Factory.createSquareKeyPair(Constants.BASE_SQUARE_KEY_PAIR, utility);
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

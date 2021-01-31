import java.util.ArrayList;

public class MemberPostsThread extends Thread implements IMemberPostsThread {
    private String info;
    private String uniqueId;
    private String[] msg;
    private ISquare square;
    private IUtility utility;
    private boolean workDone;
    private ArrayList<PostMessage> allPosts;
    private IFactory factory;

    public MemberPostsThread(String info, String uniqueId, String[] msg, ISquare square, IUtility utility, IFactory factory) {
        this.info = info;
        this.uniqueId = uniqueId;
        this.msg = msg;
        this.square = square;
        this.utility = utility;
        workDone = false;
        allPosts = new ArrayList<>();
        this.factory = factory;
    }

    @Override
    public void run() {
        try {
            getPostsFromOtherMembers();
        } catch (Exception ie) {
            ie.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public boolean isWorkDone() {
        return workDone;
    }

    private void getPostsFromOtherMembers() {
        if (!info.contains(uniqueId) && !info.startsWith(Constants.STAR)) {
            String[] member = info.split(Constants.DATA_SEPARATOR);
            IClient client = factory.createClient(Constants.BASE_CLIENT, member[2], Integer.valueOf(member[3]),
                    square.getInvite());
            String response = client.sendMessage(Constants.READ_COMMAND + Constants.COMMAND_DATA_SEPARATOR + msg[0]
                    + Constants.COMMAND_DATA_SEPARATOR + uniqueId, false);
            if (!response.equals(Constants.EMPTY_STRING)) {
                String[] responseSplit = response.split(Constants.COLON);
                if (responseSplit.length == 2 && responseSplit[0].equals(Constants.OK_RESULT)
                        && !responseSplit[1].equals(Constants.EMPTY_STRING)) {
                    processPostData(responseSplit, member);
                }
            }
        }

        workDone = true;
    }

    public PostMessage[] getAllPosts() {
        return allPosts.toArray(new PostMessage[allPosts.size()]);
    }

    public void processPostData(String[] responseSplit, String[] member) {
        ISquareKeyPair tempKeys = factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility);
        tempKeys.setPrivateKeyFromBase64(utility.readFile(Constants.PRIVATE_KEY_FILE));
        String[] decryptData = responseSplit[1].split(Constants.COMMAND_DATA_SEPARATOR);
        if (decryptData.length > 1) {
            String pwd = tempKeys.decryptFromBase64(decryptData[0]);
            String decrypted = utility.decrypt(decryptData[1], pwd);
            if (!decrypted.equals(Constants.EMPTY_STRING)) {
                String[] msgs = decrypted.split(Constants.COMMAND_DATA_SEPARATOR);
                for (String m : msgs) {
                    String[] m1 = m.split(Constants.FILE_DATA_SEPARATOR, 2);
                    long millis = Long.parseLong(m1[0]);
                    allPosts.add(new PostMessage(millis, m));
                    if (m.indexOf(Constants.IMAGE_MARKER) > Constants.NOT_FOUND_IN_STRING) {
                        processGetImageFile(m, member);
                    }
                }
            }
        }
    }

    private void processGetImageFile(String data, String[] member) {
        String[] message = data.split(Constants.DATA_SEPARATOR);
        String fileName = message[1]
                .substring(message[1].indexOf(Constants.END_SQUARE_BRACKET) + Constants.END_SQUARE_BRACKET.length());

        if (utility.checkFileExists(fileName)) {
            return;
        }

        IClient client = factory.createClient(Constants.BASE_CLIENT, member[2], Integer.valueOf(member[3]),
                square.getInvite());

        String response = client.sendMessage(Constants.REQUEST_FILE_COMMAND + Constants.COMMAND_DATA_SEPARATOR
                + fileName + Constants.COMMAND_DATA_SEPARATOR + uniqueId, false);

        SquareResponse responseData = new SquareResponse(response);

        ISquareKeyPair keys = factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility);
        keys.setPrivateKeyFromBase64(utility.readFile(Constants.PRIVATE_KEY_FILE));

        String[] fileData = responseData.getMessage().split(Constants.COMMAND_DATA_SEPARATOR);

        String key = keys.decryptFromBase64(fileData[0]);

        String f = utility.decrypt(fileData[1], key);

        byte[] imageFile = utility.convertFromBase64(f);

        utility.writeBinaryFile(fileName, imageFile);
    }
}

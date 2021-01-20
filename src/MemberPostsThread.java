public class MemberPostsThread extends Thread implements IMemberPostsThread {
    private String info;
    private String file;
    private String uniqueId;
    private String[] msg;
    private ISquare square;
    private IUtility utility;
    private boolean workDone;

    public MemberPostsThread(String info, String file, String uniqueId, String[] msg, ISquare square, IUtility utility) {
        this.info = info;
        this.file = file;
        this.uniqueId = uniqueId;
        this.msg = msg;
        this.square = square;
        this.utility = utility;
        workDone = false;
    }

    @Override
    public void run() {
        try {
            getPostsFromOtherMembers();
        }
        catch (Exception ie) {
            ie.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public boolean isWorkDone() {
        return workDone;
    }

    private void getPostsFromOtherMembers() {
        if (!info.contains(uniqueId)) {
            String[] member = info.split(Constants.DATA_SEPARATOR);
            IClient client = Factory.createClient(Constants.BASE_CLIENT, member[2], Integer.valueOf(member[3]),
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

    public void processPostData(String[] responseSplit, String[] member) {
        ISquareKeyPair tempKeys = Factory.createSquareKeyPair(Constants.BASE_SQUARE_KEY_PAIR);
        tempKeys.setPrivateKeyFromBase64(utility.readFile(Constants.PRIVATE_KEY_FILE));
        String[] decryptData = responseSplit[1].split(Constants.COMMAND_DATA_SEPARATOR);
        if (decryptData.length > 1){
            String pwd = tempKeys.decryptFromBase64(decryptData[0]);
            String decrypted = utility.decrypt(decryptData[1], pwd);
            if (!decrypted.equals(Constants.EMPTY_STRING)) {
                String newLine = Constants.NEWLINE;
                if (!utility.checkFileExists(file)) {
                    newLine = Constants.EMPTY_STRING;
                }
                String posts = newLine
                        + decrypted.replace(Constants.COMMAND_DATA_SEPARATOR, Constants.NEWLINE);
                utility.appendToFile(file, posts);
                String[] msgs = decrypted.split(Constants.COMMAND_DATA_SEPARATOR);
                String[] lastMsg = msgs[msgs.length - 1].split(Constants.DATA_SEPARATOR);
                msg[0] = lastMsg[0];
                for (String m : msgs) {
                    if (m.indexOf(Constants.IMAGE_MARKER) > Constants.NOT_FOUND_IN_STRING) {
                        processGetImageFile(m, member);
                    }
                }
            }
        }
    }

    private void processGetImageFile(String data, String[] member) {
        String[] message = data.split(Constants.DATA_SEPARATOR);

        IClient client = Factory.createClient(Constants.BASE_CLIENT, member[2], Integer.valueOf(member[3]),
            square.getInvite());

        String fileName = message[1].substring(message[1].indexOf("]") + 1);
        String response = client.sendMessage(Constants.REQUEST_FILE_COMMAND + Constants.COMMAND_DATA_SEPARATOR + fileName
            + Constants.COMMAND_DATA_SEPARATOR + uniqueId, false);

        SquareResponse responseData = new SquareResponse(response);

        ISquareKeyPair keys = Factory.createSquareKeyPair(Constants.BASE_SQUARE_KEY_PAIR);
        keys.setPrivateKeyFromBase64(utility.readFile(Constants.PRIVATE_KEY_FILE));

        String[] fileData = responseData.getMessage().split(Constants.COMMAND_DATA_SEPARATOR);

        String key = keys.decryptFromBase64(fileData[0]);

        String f = utility.decrypt(fileData[1], key);

        byte[] imageFile = utility.convertFromBase64(f);

        FileWriteResponse result = utility.writeBinaryFile(fileName, imageFile);
    }
}

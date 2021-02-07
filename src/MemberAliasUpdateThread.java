public class MemberAliasUpdateThread extends Thread implements IMemberAliasUpdateThread {
    private boolean workDone;
    private IFactory factory;
    private String[] aliases;
    private String info;
    private String uniqueId;
    private ISquare square;
    private IUtility utility;
    private ISquareKeyPair keys;

    public MemberAliasUpdateThread(IFactory factory, String info, String uniqueId, ISquare square, IUtility utility) {
        this.factory = factory;
        this.info = info;
        this.uniqueId = uniqueId;
        this.square = square;
        this.utility = utility;
        this.workDone = false;
    }

    @Override
    public void run() {
        try {
            keys = factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility);
            aliases = utility.readFile(square.getSafeLowerName() + Constants.ALIAS_FILE_EXT).split(Constants.READ_FILE_DATA_SEPARATOR);
            workDone = sendAliasesToOtherMembers();
        } catch (Exception ie) {
            ie.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public boolean isWorkDone() {
        return workDone;
    }

    private boolean sendAliasesToOtherMembers(){
        if (!info.contains(uniqueId) && !info.startsWith(Constants.STAR)) {
            String[] member = info.split(Constants.DATA_SEPARATOR);
            IClient client = factory.createClient(Constants.BASE_CLIENT, member[2], Integer.valueOf(member[3]),
                    square.getInvite());
            keys.setPublicKeyFromBase64(member[1]);
            for (String a : aliases) {
                String[] datas = a.split(Constants.QUESTION_MARK_SPLIT);
                String[] addresses = datas[1].split(Constants.FORWARD_SLASH);
                for (String address : addresses) {
                    processAliasMembers(address, client, datas[0]);
                }
            }
        }
        return true;
    }

    private void processAliasMembers(String data, IClient client, String uniqueId) {
        String[] alias = data.split(Constants.COLON);
        String password = utility.generateRandomString(Constants.ENCRYPTION_KEY_LENGTH);
        String encryptedPassword = keys.encryptToBase64(password); 
        
        String encryptedData = utility.encrypt(Constants.REGISTER_ALIAS_COMMAND
            + Constants.COMMAND_DATA_SEPARATOR + Constants.NULL_TEXT + Constants.FILE_DATA_SEPARATOR + alias[0]
            + Constants.FILE_DATA_SEPARATOR + alias[1] + Constants.FILE_DATA_SEPARATOR
            + uniqueId, password);
        String request = encryptedPassword + Constants.COMMAND_DATA_SEPARATOR + encryptedData;
        client.sendMessage(request, true);
    }
}

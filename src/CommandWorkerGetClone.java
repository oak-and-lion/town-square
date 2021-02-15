import java.util.ArrayList;
import java.util.List;

public class CommandWorkerGetClone extends CommandWorkerBase implements ICommandWorker {
    private IFactory factory;

    public CommandWorkerGetClone(IUtility utility, ISquare square, IDialogController parent, IFactory factory) {
        super(utility, square, parent);
        this.factory = factory;
    }

    public List<BooleanString> doWork(String commandArgs) {
        String[] args = commandArgs.split(Constants.SPACE);
        ArrayList<BooleanString> result = new ArrayList<>();
        BooleanString r = getClone(args);
        result.add(r);
        return result;
    }

    private BooleanString getClone(String[] args) {
        String hostName = args[0];
        int port = Integer.parseInt(args[1]);
        String squareId = args[2];
        String password = args[3];
        IClient client = factory.createClient(Constants.BASE_CLIENT, hostName, port, squareId, parent.getParent());
        String pkey = client.sendMessage(Constants.REQUEST_PUBLIC_KEY_COMMAND, Constants.DO_NOT_ENCRYPT_CLIENT_TRANSFER, Constants.REQUEST_PUBLIC_KEY_COMMAND);
        SquareResponse resp = new SquareResponse(pkey);
        if (resp.getCode().equals(Constants.OK_RESULT)) {
            ISquareKeyPair keyPair = factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility);
            keyPair.setPublicKeyFromBase64(resp.getMessage());
            String key = utility.generateRandomString(Constants.ENCRYPTION_KEY_LENGTH);
            String e = utility.encrypt(utility.concatStrings(Constants.CLONE_COMMAND, Constants.COMMAND_DATA_SEPARATOR, password
                    , Constants.COMMAND_DATA_SEPARATOR, utility.readFile(Constants.IP_FILE)
                    , Constants.COMMAND_DATA_SEPARATOR, utility.readFile(Constants.PORT_FILE)), key);
            String f = keyPair.encryptToBase64(key);
            String result = client.sendMessage(utility.concatStrings(f, Constants.COMMAND_DATA_SEPARATOR, e), Constants.ENCRYPT_CLIENT_TRANSFER, Constants.CLONE_COMMAND);
            SquareResponse cloneResponse = new SquareResponse(result);
            if (cloneResponse.getCode().equals(Constants.OK_RESULT)) {
                StringBuilder pwdPadded = new StringBuilder(password);
                for (int x = password.length(); x < Constants.ENCRYPTION_KEY_LENGTH; x++) {
                    pwdPadded.append(Constants.UNDERSCORE);
                }
                String b64 = utility.decrypt(cloneResponse.getMessage(), pwdPadded.toString());
                byte[] data = utility.convertFromBase64(b64);

                utility.deleteFile(utility.concatStrings(square.getSafeLowerName(), Constants.CLONE_FILE_EXT));
                utility.writeBinaryFile(utility.concatStrings(square.getSafeLowerName(), Constants.CLONE_FILE_EXT), data);
                parent.showCloneMessage();
                return new BooleanString(true, utility.concatStrings(square.getSafeLowerName(), Constants.CLONE_FILE_EXT));
            }
        }

        return new BooleanString(false, Constants.EMPTY_STRING);
    }
}

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
        IClient client = factory.createClient(Constants.BASE_CLIENT, args[1], Integer.valueOf(args[2]), args[3]);
        String pkey = client.sendMessage(Constants.REQUEST_PUBLIC_KEY_COMMAND, false);
        SquareResponse resp = new SquareResponse(pkey);
        if (resp.getCode().equals(Constants.OK_RESULT)) {
            ISquareKeyPair keyPair = factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility);
            keyPair.setPublicKeyFromBase64(resp.getMessage());
            String key = utility.generateRandomString(Constants.ENCRYPTION_KEY_LENGTH);
            String e = utility.encrypt(Constants.CLONE_COMMAND + Constants.COMMAND_DATA_SEPARATOR + args[4], key);
            String f = keyPair.encryptToBase64(key);
            String result = client.sendMessage(f + Constants.COMMAND_DATA_SEPARATOR + e, true);
            SquareResponse cloneResponse = new SquareResponse(result);
            if (cloneResponse.getCode().equals(Constants.OK_RESULT)) {
                StringBuilder pwdPadded = new StringBuilder(args[4]);
                for (int x = args[4].length(); x < Constants.ENCRYPTION_KEY_LENGTH; x++) {
                    pwdPadded.append(Constants.UNDERSCORE);
                }
                String b64 = utility.decrypt(cloneResponse.getMessage(), pwdPadded.toString());
                byte[] data = utility.convertFromBase64(b64);

                utility.deleteFile(square.getSafeLowerName() + Constants.CLONE_FILE_EXT);
                utility.writeBinaryFile(square.getSafeLowerName() + Constants.CLONE_FILE_EXT, data);
                parent.showCloneMessage();
                return new BooleanString(true, square.getSafeLowerName() + Constants.CLONE_FILE_EXT);
            }
        }

        return new BooleanString(false, Constants.EMPTY_STRING);
    }
}

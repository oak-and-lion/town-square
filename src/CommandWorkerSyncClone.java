import java.util.ArrayList;
import java.util.List;

public class CommandWorkerSyncClone extends CommandWorkerBase implements ICommandWorker {
    private IFactory factory;

    public CommandWorkerSyncClone(IUtility utility, ISquare square, IDialogController parent, IFactory factory) {
        super(utility, square, parent);
        this.factory = factory;
    }

    public List<BooleanString> doWork(String commandArgs) {
        String[] args = commandArgs.split(Constants.SPACE);
        ArrayList<BooleanString> result = new ArrayList<>();
        BooleanString r = syncClone(args);
        result.add(r);
        return result;
    }

    private BooleanString syncClone(String[] args) {
        String hostName = args[0];
        int port = Integer.parseInt(args[1]);
        String squareId = args[2];
        IClient client = factory.createClient(Constants.BASE_CLIENT, hostName, port, squareId, parent.getParent());
        String pkey = client.sendMessage(Constants.REQUEST_PUBLIC_KEY_COMMAND, Constants.DO_NOT_ENCRYPT_CLIENT_TRANSFER, Constants.REQUEST_PUBLIC_KEY_COMMAND);
        SquareResponse resp = new SquareResponse(pkey);
        if (resp.getCode().equals(Constants.OK_RESULT)) {
            ISquareKeyPair keyPair = factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility);
            keyPair.setPublicKeyFromBase64(resp.getMessage());
            String key = utility.generateRandomString(Constants.ENCRYPTION_KEY_LENGTH);
            String e = utility.encrypt(utility.concatStrings(squareId, utility.readFile(Constants.UNIQUE_ID_FILE)), key);
            String f = keyPair.encryptToBase64(key);
            String result = client.sendMessage(utility.concatStrings(f, Constants.COMMAND_DATA_SEPARATOR, e), Constants.ENCRYPT_CLIENT_TRANSFER, Constants.SYNC_CLONE_COMMAND);
            SquareResponse cloneResponse = new SquareResponse(result);
            if (cloneResponse.getCode().equals(Constants.OK_RESULT)) {
                // process the returned file
            }
        }

        return new BooleanString(false, Constants.EMPTY_STRING);
    }
}

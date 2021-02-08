import java.util.ArrayList;
import java.util.Arrays;

public class SquareController implements ISquareController {
    private IUtility utility;
    private IDialogController sampleController;
    private ISquareKeyPair keys;
    private ILogIt logger;
    private IFactory factory;

    public SquareController(IUtility mainUtility, IDialogController controller, ILogIt logger, ISquareKeyPair keyPair,
            IFactory factory) {
        utility = mainUtility;
        sampleController = controller;
        keys = keyPair;
        this.logger = logger;
        this.factory = factory;
    }

    public SquareResponse processRequest(String request) {
        SquareResponse result = new SquareResponse(Constants.MALFORMED_REQUEST_RESULT,
                Constants.MALFORMED_REQUEST_MESSAGE);
        boolean okToProcess = true;

        // command structure
        // 0 == encryption flag
        // 1 == square invite id
        // 2 == command
        // 3+ == command arguments
        String[] split = request.split(Constants.COMMAND_DATA_SEPARATOR);
        if (split.length > 2) {
            String[] newSplit;
            if (split[0].equals(Constants.ENCRYPTION_FLAG)) {
                newSplit = decryptArray(split);
                if (newSplit.length == 0) {
                    okToProcess = false;
                }
            } else {
                newSplit = split;
            }

            if (okToProcess) {
                result = processRequestCommand(newSplit);
            }
        }

        return result;
    }

    private String[] decryptArray(String[] split) {
        String encryptionFlag = split[0];
        String command = split[2].trim();
        String inviteId = split[1].trim();
        String encrypted = split[3];
        
        ArrayList<String> result = new ArrayList<>();

        ISquare square = sampleController.getSquareByInvite(inviteId);
        if (square != null) {
            String password = keys.decryptFromBase64(command).trim();
            try {
                String raw = utility.decrypt(encrypted, password);

                String[] data = raw.split(Constants.COMMAND_DATA_SEPARATOR);

                result.add(encryptionFlag);
                result.add(inviteId);
                result.addAll(Arrays.asList(data));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result.toArray(new String[result.size()]);
    }

    private SquareResponse processRequestCommand(String[] split) {
        String command = split[2].trim();
        String inviteId = split[1].trim();

        ISquare square = factory.findSquareByCommand(command, inviteId, sampleController);

        if (square == null) {
            return new SquareResponse(Constants.MALFORMED_REQUEST_RESULT, Constants.MALFORMED_REQUEST_MESSAGE);
        }

        ISquareWorker worker = factory.createSquareWorker(command, utility, sampleController, logger);
        return worker.doWork(square, split);
    }

    public boolean isHiding() {
        return sampleController.getParent().isHidingServer();
    }
}

import java.util.ArrayList;
import java.util.List;

public class CommandWorkerAck extends CommandWorkerBase implements ICommandWorker {
    private IFactory factory;
    public CommandWorkerAck(IUtility utility, ISquare square, IDialogController parent) {
        super(utility, square, parent);
        this.factory = square.getFactory();
    }

    public List<BooleanString> doWork(String commandArgs) {
        ArrayList<BooleanString> result = new ArrayList<>();
        String[] info = commandArgs.split(Constants.FILE_DATA_SEPARATOR);
        IClient client = factory.createClient(Constants.BASE_CLIENT, info[0], Integer.valueOf(info[1]), square.getInvite());
        String response = client.sendMessage(Constants.ACK_COMMAND, Constants.DO_NOT_ENCRYPT_CLIENT_TRANSFER);
        if (response.equals(Constants.EMPTY_STRING)) {
            result.add(new BooleanString(false, Constants.EMPTY_STRING));
        } else {
            result.add(new BooleanString(true, Constants.EMPTY_STRING));
        }
        return result;
    }
}

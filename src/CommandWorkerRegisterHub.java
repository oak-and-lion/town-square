import java.util.ArrayList;
import java.util.List;

public class CommandWorkerRegisterHub extends CommandWorkerBase implements ICommandWorker {
    public CommandWorkerRegisterHub(IUtility utility, ISquare square, IDialogController parent) {
        super(utility, square, parent);
    }

    public List<BooleanString> doWork(String commandArgs) {
        ArrayList<BooleanString> list = new ArrayList<>();
        list.add(new BooleanString(registerHub(commandArgs), Constants.EMPTY_STRING));
        return list;
    }

    boolean registerHub(String address) {
        FileWriteResponse result = utility.writeFile(Constants.HUB_REGISTRATION_FILE_EXT, address);

        return result.isSuccessful();
    }
}

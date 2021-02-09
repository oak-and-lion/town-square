import java.util.ArrayList;
import java.util.List;

public class CommandWorkerLicense extends CommandWorkerBase implements ICommandWorker {
    public CommandWorkerLicense(IUtility utility, ISquare square, IDialogController parent) {
        super(utility, square, parent);
    }

    public List<BooleanString> doWork(String commandArgs) {
        ArrayList<BooleanString> result = new ArrayList<>();
        parent.showLicense();
        result.add(new BooleanString(true, Constants.EMPTY_STRING));
        return result;
    }
}

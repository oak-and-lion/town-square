import java.util.ArrayList;
import java.util.List;

public class CommandWorkerAbout extends CommandWorkerBase implements ICommandWorker {
    public CommandWorkerAbout(IUtility utility, ISquare square, IDialogController parent) {
        super(utility, square, parent);
    }

    public List<BooleanString> doWork(String commandArgs) {
        ArrayList<BooleanString> result = new ArrayList<>();
        parent.showAbout();
        result.add(new BooleanString(true, Constants.EMPTY_STRING));
        return result;
    }
}

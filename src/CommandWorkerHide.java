import java.util.ArrayList;
import java.util.List;

public class CommandWorkerHide extends CommandWorkerBase implements ICommandWorker {
    public CommandWorkerHide(IUtility utility, ISquare square, IDialogController parent) {
        super(utility, square, parent);
    }

    public List<BooleanString> doWork(String commandArgs) {
        ArrayList<BooleanString> result = new ArrayList<>();
        parent.getParent().hideServer();
        result.add(new BooleanString(true, Constants.EMPTY_STRING));
        return result;
    }
}

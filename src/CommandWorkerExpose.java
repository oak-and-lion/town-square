import java.util.ArrayList;
import java.util.List;

public class CommandWorkerExpose extends CommandWorkerBase implements ICommandWorker {
    public CommandWorkerExpose(IUtility utility, ISquare square, IDialogController parent) {
        super(utility, square, parent);
    }

    public List<BooleanString> doWork(String commandArgs) {
        ArrayList<BooleanString> result = new ArrayList<>();
        parent.getParent().exposeServer();
        result.add(new BooleanString(true, Constants.EMPTY_STRING));
        return result;
    }
}

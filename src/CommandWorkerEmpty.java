import java.util.ArrayList;
import java.util.List;

public class CommandWorkerEmpty extends CommandWorkerBase implements ICommandWorker {
    public CommandWorkerEmpty(IUtility utility, ISquare square, IDialogController parent) {
        super(utility, square, parent);
    }

    public List<BooleanString> doWork(String commandArgs) {
        ArrayList<BooleanString> result = new ArrayList<>();
        result.add(new BooleanString(false, Constants.EMPTY_STRING));
        return result;
    }
    
}

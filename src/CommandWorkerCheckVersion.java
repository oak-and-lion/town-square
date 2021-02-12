import java.util.ArrayList;
import java.util.List;

public class CommandWorkerCheckVersion extends CommandWorkerBase implements ICommandWorker {
    private IFactory factory;

    public CommandWorkerCheckVersion(IUtility utility, ISquare square, IDialogController parent, IFactory factory) {
        super(utility, square, parent);
        this.factory = factory;
    }

    public List<BooleanString> doWork(String commandArgs) {
        IVersionChecker versionChecker = factory.createVersionChecker(Constants.BASE_VERSION_CHECKER, utility,
                utility.readFile(Constants.UNIQUE_ID_FILE));
        versionChecker.start();
        while (!versionChecker.isDone()) {
            try {
                Thread.sleep(Constants.DEFAULT_WAIT_TIME);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        ArrayList<BooleanString> result = new ArrayList<>();
        result.add(new BooleanString(true, Constants.EMPTY_STRING));
        return result;
    }
}

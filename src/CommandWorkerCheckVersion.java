import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandWorkerCheckVersion extends CommandWorkerBase implements ICommandWorker {
    private IFactory factory;
    private ILogIt errorLogger;

    public CommandWorkerCheckVersion(IUtility utility, ISquare square, IDialogController parent, IFactory factory) {
        super(utility, square, parent);
        this.factory = factory;
        this.errorLogger = factory.createLogger(Constants.ERROR_LOGGER, Constants.ERROR_LOG_FILE, utility, this.parent);
    }

    public List<BooleanString> doWork(String commandArgs) {
        IVersionChecker versionChecker = factory.createVersionChecker(Constants.BASE_VERSION_CHECKER, utility,
                utility.readFile(Constants.UNIQUE_ID_FILE), parent.getParent());
        versionChecker.start();
        while (!versionChecker.isDone()) {
            try {
                Thread.sleep(Constants.DEFAULT_WAIT_TIME);
            } catch (InterruptedException ie) {
                errorLogger.logInfo(utility.concatStrings(ie.getMessage(), Constants.NEWLINE, Arrays.toString(ie.getStackTrace())));
                Thread.currentThread().interrupt();
            }
        }

        ArrayList<BooleanString> result = new ArrayList<>();
        result.add(new BooleanString(true, Constants.EMPTY_STRING));
        return result;
    }
}

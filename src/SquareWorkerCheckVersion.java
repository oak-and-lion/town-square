public class SquareWorkerCheckVersion extends SquareWorkerBase implements ISquareWorker {
    public SquareWorkerCheckVersion(IUtility utility, String command) {
        super(utility, command);
    }

    public SquareResponse doWork(ISquare square, String[] args) {
        return new SquareResponse(buildResult(Constants.OK_RESULT, Constants.VERSION));
    }
}

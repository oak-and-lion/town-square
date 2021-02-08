public class SquareWorkerFailure extends SquareWorkerBase implements ISquareWorker {
    public SquareWorkerFailure(IUtility utility, String command) {
        super(utility, command);
    }

    public SquareResponse doWork(ISquare square, String[] args) {
        return new SquareResponse(buildResult(Constants.DECRYPTION_FAILURE_RESULT, Constants.DECRYPTION_FAILURE_MESSAGE));
    }
}
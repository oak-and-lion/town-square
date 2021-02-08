public class SquareWorkerEmpty extends SquareWorkerBase implements ISquareWorker {
    
    public SquareWorkerEmpty(IUtility utility, String command) {
        super(utility, command);
    }
    public SquareResponse doWork(ISquare square, String[] args) {
        return new SquareResponse(Constants.UNKNOWN_COMMAND_RESULT, command);
    }
}

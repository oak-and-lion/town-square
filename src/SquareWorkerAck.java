public class SquareWorkerAck extends SquareWorkerBase implements ISquareWorker {
    public SquareWorkerAck(IUtility utility, String command) {
        super(utility, command);
    }

    public SquareResponse doWork(ISquare square, String[] args) {
        return new SquareResponse(Constants.OK_RESULT, Constants.ACK_BACK);
    }
}

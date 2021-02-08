public class SquareWorkerPublicKeyRequest extends SquareWorkerBase implements ISquareWorker {
    public SquareWorkerPublicKeyRequest(IUtility utility, String command) {
        super(utility, command);
    }

    public SquareResponse doWork(ISquare square, String[] args) {
        String key = utility.readFile(Constants.PUBLIC_KEY_FILE);
        return new SquareResponse(Constants.OK_RESULT, key);
    }
}

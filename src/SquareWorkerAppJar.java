public class SquareWorkerAppJar extends SquareWorkerBase implements ISquareWorker {
    public SquareWorkerAppJar(IUtility utility, String command) {
        super(utility, command);
    }

    public SquareResponse doWork(ISquare square, String[] args) {
        return new SquareResponse();
    }
}

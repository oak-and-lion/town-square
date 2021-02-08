public class SquareWorkerAppJar extends SquareWorkerBase implements ISquareWorker {
    public SquareWorkerAppJar(IUtility utility, String command) {
        super(utility, command);
    }

    public SquareResponse doWork(ISquare square, String[] args) {
        byte[] data = utility.readBinaryFile(Constants.JAR_FILE);
        
        return new SquareResponse(Constants.OK_RESULT, utility.convertToBase64(data));
    }
}

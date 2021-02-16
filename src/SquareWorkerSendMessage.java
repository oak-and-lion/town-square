public class SquareWorkerSendMessage extends SquareWorkerBase implements ISquareWorker {
    
    public SquareWorkerSendMessage(IUtility utility, String command) {
        super(utility, command);
    }
    public SquareResponse doWork(ISquare square, String[] args) {
        String[] message = args[3].split(Constants.FILE_DATA_SEPARATOR);
        long millis = Long.parseLong(message[0]);
        square.addPostMessage(new PostMessage(millis, args[3]));
        return new SquareResponse(Constants.OK_RESULT, command);
    }
}

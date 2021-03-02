public class SquareWorkerSendAttachment extends SquareWorkerBase implements ISquareWorker {
    
    public SquareWorkerSendAttachment(IUtility utility, String command) {
        super(utility, command);
    }
    public SquareResponse doWork(ISquare square, String[] args) {
        String[] attachment = args[3].split(Constants.FILE_DATA_SEPARATOR);
        utility.writeBinaryFile(attachment[1], utility.convertFromBase64(attachment[0]));
        return new SquareResponse(Constants.OK_RESULT, command);
    }
}

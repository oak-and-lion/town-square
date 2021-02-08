public class SquareWorkerClone extends SquareWorkerBase implements ISquareWorker {
    public SquareWorkerClone(IUtility utility, String command) {
        super(utility, command);
    }

    public SquareResponse doWork(ISquare square, String[] args) {
        return new SquareResponse(buildClone(square, args));
    }

    private String buildClone(ISquare square, String[] args) {
        IDialogController controller = square.getSampleController();
        ICommandController cmdController = controller.getCommandController();
        BooleanString[] result = cmdController
                .processCommand(Constants.FORWARD_SLASH + args[2] + Constants.SPACE + args[3], square);
        if (result.length > 0 && result[0].getBoolean()) {
            return buildResult(Constants.OK_RESULT, result[0].getString());
        }
        return buildResult(Constants.MALFORMED_REQUEST_RESULT, Constants.MALFORMED_REQUEST_MESSAGE);
    }
}

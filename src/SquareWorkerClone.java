public class SquareWorkerClone extends SquareWorkerBase implements ISquareWorker {

    public SquareWorkerClone(IUtility utility, String command) {
        super(utility, command);
    }

    public SquareResponse doWork(ISquare square, String[] args) {
        return new SquareResponse(buildClone(square, args));
    }

    private String buildClone(ISquare square, String[] args) {
        String uniqueId = args[1];
        String command = args[2];
        String password = args[3];
        String ip = args[4];
        String port = args[5];
        IDialogController controller = square.getSampleController();
        ICommandController cmdController = controller.getCommandController();
        StringBuilder temp = new StringBuilder();
        temp.append(Constants.FORWARD_SLASH);
        temp.append(command);
        temp.append(Constants.SPACE);
        temp.append(password);
        temp.append(Constants.COMMAND_DATA_SEPARATOR);
        temp.append(uniqueId);
        temp.append(Constants.COMMAND_DATA_SEPARATOR);
        temp.append(ip);
        temp.append(Constants.COMMAND_DATA_SEPARATOR);
        temp.append(port);
        BooleanString[] result = cmdController.processCommand(temp.toString(), square);
        if (result.length > 0 && result[0].getBoolean()) {
            return buildResult(Constants.OK_RESULT, result[0].getString());
        }
        return buildResult(Constants.MALFORMED_REQUEST_RESULT, Constants.MALFORMED_REQUEST_MESSAGE);
    }
}

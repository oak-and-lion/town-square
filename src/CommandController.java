import java.util.ArrayList;

public class CommandController implements ICommandController {
    private IUtility utility;
    private IDialogController parent;
    private IFactory factory;

    public CommandController(IUtility utility, IDialogController parent, IFactory factory) {
        this.utility = utility;
        this.parent = parent;
        this.factory = factory;
    }

    public BooleanString[] processCommand(String command, ISquare square) {
        if (!command.startsWith(Constants.COMMAND_PREFIX)) {
            return new BooleanString[] { new BooleanString(false, Constants.MALFORMED_REQUEST_MESSAGE) };
        }

        command = command.replace(Constants.COMMAND_PREFIX, Constants.EMPTY_STRING);
        String[] commandBreakdown = command.split(Constants.SPACE);
        String[] commandArgs = command.split(Constants.SPACE, 2);
        String args = Constants.EMPTY_STRING;
        if (commandArgs.length > 1) {
            args = commandArgs[1];
        }

        String cmd = commandBreakdown[0].replace(Constants.SPACE, Constants.EMPTY_STRING).toLowerCase().trim();

        ArrayList<BooleanString> result = new ArrayList<>();

        ICommandWorker worker = factory.createCommandWorker(cmd, utility, square, parent);

        result.addAll(worker.doWork(args));

        return result.toArray(new BooleanString[result.size()]);
    }
}

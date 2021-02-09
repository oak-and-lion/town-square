import java.util.ArrayList;
import java.util.List;

public class CommandWorkerHelp extends CommandWorkerBase implements ICommandWorker {
    private ArrayList<String> commands;

    public CommandWorkerHelp(IUtility utility, ISquare square, IDialogController parent) {
        super(utility, square, parent);
        this.commands = new ArrayList<>();
        commands.add(Constants.ABOUT_COMMAND);
        commands.add(Constants.BLOCK_COMMAND);
        commands.add(Constants.CLONE_COMMAND);
        commands.add(Constants.DNA_COMMAND);
        commands.add(Constants.EXPOSE_COMMAND);
        commands.add(Constants.HELP_COMMAND);
        commands.add(Constants.HIDE_COMMAND);
        commands.add(Constants.LICENSE_COMMAND);
        commands.add(Constants.NICKNAME_COMMAND);
        commands.add(Constants.PAUSE_COMMAND);
        commands.add(Constants.UNBLOCK_COMMAND);
        commands.add(Constants.UNPAUSE_COMMAND);
    }

    public List<BooleanString> doWork(String commandArgs) {
        ArrayList<BooleanString> result = new ArrayList<>();
        parent.showList(commands.toArray(new String[commands.size()]), Constants.COMMANDS_TITLE,
                Constants.COMMANDS_HEADER);
        result.add(new BooleanString(true, Constants.EMPTY_STRING));
        return result;
    }
}

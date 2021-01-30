import java.util.ArrayList;

public class CommandController implements ICommandController {
    private IUtility utility;

    public CommandController(IUtility utility) {
        this.utility = utility;
    }

    public Boolean[] processCommand(String command, ISquare square) {
        if (!command.startsWith(Constants.COMMAND_PREFIX)) {
            return new Boolean[] {false};
        }

        command = command.replace(Constants.COMMAND_PREFIX, Constants.EMPTY_STRING);
        String[] commandBreakdown = command.split(Constants.SPACE);

        String cmd = commandBreakdown[0].replace(Constants.SPACE, Constants.EMPTY_STRING).toLowerCase().trim();

        ArrayList<Boolean> result = new ArrayList<Boolean>();
        if (cmd.equals(Constants.BLOCK_COMMAND)) {
            String[] users = commandBreakdown[1].split(Constants.SEMI_COLON);
            for (String user : users) {
                result.add(blockUser(user, square));
            }
        } else {
            result.add(false);
        }

        return result.toArray(new Boolean[result.size()]);
    }

    public boolean blockUser(String user, ISquare square) {
        if (user == null) {
            return false;
        }
        user = user.trim();
        if (user.equals(Constants.EMPTY_STRING)) {
            return false;
        }
        String[] results = utility.searchFile(square.getSafeLowerName() + Constants.MEMBERS_FILE_EXT, user,
                Constants.SEARCH_STARTS_WITH);
        if (results.length > 0) {
            FileWriteResponse response = utility.appendToFile(square.getSafeLowerName() + Constants.BLOCK_FILE_EXT,
                    user);
            return response.isSuccessful();
        }

        return false;
    }
}

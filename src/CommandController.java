public class CommandController implements ICommandController {
    private IUtility utility;

    public CommandController(IUtility utility) {
        this.utility = utility;
    }
    public void processCommand(String command, ISquare square) {
        if (!command.startsWith(Constants.COMMAND_PREFIX)) {
            return;
        }

        String[] commandBreakdown = command.split(Constants.SPACE);

        String cmd = commandBreakdown[0].replace(Constants.SPACE, Constants.EMPTY_STRING).toLowerCase().trim();

        if (cmd.equals(Constants.BLOCK_COMMAND)) {
            blockUser(commandBreakdown[1].split(Constants.SEMI_COLON), square);
        }
    }

    public void blockUser(String[] users, ISquare square) {
        for(String user : users) {
            user = user.trim();
            if (user.equals(Constants.EMPTY_STRING)) {
                continue;
            }
            String[] results = utility.searchFile(square.getSafeLowerName() + Constants.MEMBERS_FILE_EXT, user, Constants.SEARCH_STARTS_WITH);
            if (results.length > -1) {
                utility.appendToFile(square.getSafeLowerName() + Constants.BLOCK_FILE_EXT, user);
            }
        }
    }
}

import java.util.ArrayList;
import java.util.List;

public class CommandWorkerUnblock extends CommandWorkerBase implements ICommandWorker {
    public CommandWorkerUnblock(IUtility utility, ISquare square, IDialogController parent) {
        super(utility, square, parent);
    }

    public List<BooleanString> doWork(String commandArgs) {
        ArrayList<BooleanString> result = new ArrayList<>();
        String[] users = commandArgs.split(Constants.SEMI_COLON);
        for (String user : users) {
            result.add(new BooleanString(unblockUser(user), Constants.EMPTY_STRING));
        }

        return result;
    }

    public boolean unblockUser(String user) {
        if (square == null) {
            return false;
        }
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
            String[] memberInfo = results[0].split(Constants.FILE_DATA_SEPARATOR);
            String[] blockedMembers = utility.readFile(square.getSafeLowerName() + Constants.BLOCK_FILE_EXT)
                    .split(Constants.READ_FILE_DATA_SEPARATOR);
            StringBuilder output = new StringBuilder();
            boolean first = true;
            String newline = Constants.EMPTY_STRING;
            for (String blockedMember : blockedMembers) {
                if (!blockedMember.equals(memberInfo[4])) {
                    output.append(newline + blockedMember);
                }
                if (first) {
                    first = false;
                    newline = Constants.NEWLINE;
                }
            }
            FileWriteResponse response = utility.writeFile(square.getSafeLowerName() + Constants.BLOCK_FILE_EXT,
                    output.toString());
            return response.isSuccessful();
        }
        return false;
    }
}

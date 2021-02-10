import java.util.ArrayList;
import java.util.List;

public class CommandWorkerBlock extends CommandWorkerBase implements ICommandWorker {
    public CommandWorkerBlock(IUtility utility, ISquare square, IDialogController parent) {
        super(utility, square, parent);
    }

    public List<BooleanString> doWork(String commandArgs) {
        ArrayList<BooleanString> result = new ArrayList<>();
        String[] users = commandArgs.split(Constants.SEMI_COLON);
        for (String user : users) {
            result.add(new BooleanString(blockUser(user, square), Constants.EMPTY_STRING));
        }

        return result;
    }

    public boolean blockUser(String user, ISquare square) {
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
        String[] results = utility.searchFile(utility.concatStrings(square.getSafeLowerName(), Constants.MEMBERS_FILE_EXT), user,
                Constants.SEARCH_STARTS_WITH);
        if (results.length > 0) {
            String[] memberInfo = results[0].split(Constants.FILE_DATA_SEPARATOR);
            String[] alreadyBlocked = utility.searchFile(utility.concatStrings(square.getSafeLowerName(), Constants.BLOCK_FILE_EXT),
                    memberInfo[4], Constants.SEARCH_CONTAINS);
            if (alreadyBlocked.length < 1) {
                int lines = utility.countLinesInFile(utility.concatStrings(square.getSafeLowerName(), Constants.BLOCK_FILE_EXT));
                String newLine = Constants.EMPTY_STRING;
                if (lines > 0) {
                    newLine = Constants.NEWLINE;
                }
                FileWriteResponse response = utility.appendToFile(utility.concatStrings(square.getSafeLowerName(), Constants.BLOCK_FILE_EXT),
                utility.concatStrings(newLine, memberInfo[4]));
                return response.isSuccessful();
            }
            return false;
        }

        return false;
    }
}

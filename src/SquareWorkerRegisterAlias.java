import java.util.ArrayList;

public class SquareWorkerRegisterAlias extends SquareWorkerBase implements ISquareWorker {
    public SquareWorkerRegisterAlias(IUtility utility, String command) {
        super(utility, command);
    }

    public SquareResponse doWork(ISquare square, String[] args) {
        return new SquareResponse(buildResult(Constants.OK_RESULT, registerAlias(square, args)));
    }

    private String registerAlias(ISquare square, String[] split) {
        // command arguments
        // 3 == member info
        String[] info = split[3].split(Constants.FILE_DATA_SEPARATOR);
        if (!checkSquareAccess(square, info[3])) {
            return Constants.MALFORMED_REQUEST_MESSAGE;
        }
        String file = utility.concatStrings(square.getSafeLowerName(), Constants.ALIAS_FILE_EXT);
        ArrayList<String> memberAliases = new ArrayList<>();
        if (utility.checkFileExists(file)) {
            String[] members = utility.readFile(file).split(Constants.READ_FILE_DATA_SEPARATOR);
            for (String member : members) {
                if (!member.equals(Constants.EMPTY_STRING)) {
                    memberAliases.add(member);
                }
            }
        }

        // info order
        // 0 == null
        // 1 == ip address
        // 2 == port
        // 3 == member id
        return processAlias(info, memberAliases, file);
    }

    private String processAlias(String[] info, ArrayList<String> memberAliases, String file) {
        // command arguments
        // 3 == member id
        String alias = utility.concatStrings(info[3], Constants.QUESTION_MARK, info[1], Constants.COLON, info[2]);

        boolean found = false;
        int count = 0;
        for (String memberAlias : memberAliases) {
            if (memberAlias.startsWith(info[3])
                    && !memberAlias.contains(utility.concatStrings(info[1], Constants.COLON, info[2]))) {
                memberAlias += utility.concatStrings(Constants.FORWARD_SLASH, info[1], Constants.COLON, info[2]);
                memberAliases.set(count, memberAlias);
                found = true;
                break;
            } else if (memberAlias.startsWith(info[3])
                    && memberAlias.contains(utility.concatStrings(info[1], Constants.COLON, info[2]))) {
                return "registered";
            }
            count++;
        }

        if (found) {
            utility.deleteFile(file);
            boolean first = true;
            String newLine = Constants.EMPTY_STRING;
            for (String memberAlias : memberAliases) {
                utility.appendToFile(file, utility.concatStrings(newLine, memberAlias));
                if (first) {
                    newLine = Constants.NEWLINE;
                    first = false;
                }
            }
        } else {
            if (utility.checkFileExists(file)) {
                utility.appendToFile(file, Constants.NEWLINE);
            }
            utility.appendToFile(file, alias);
        }

        return "registered";
    }
}

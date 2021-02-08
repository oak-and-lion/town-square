public class SquareWorkerBase {
    String command;
    IUtility utility;
    
    public SquareWorkerBase(IUtility utility, String command) {
        this.command = command;
        this.utility = utility;
    }

    boolean checkSquareAccess(ISquare square, String memberId) {
        String file = square.getSafeLowerName() + Constants.MEMBERS_FILE_EXT;
        int first = utility.findFirstOccurence(file, memberId, Constants.SEARCH_CONTAINS,
                Constants.NOT_FOUND_RETURN_NEG_ONE);

        if (first < 0) {
            first = utility.findFirstOccurence(file, Constants.EXIT_SQUARE_TEXT, Constants.SEARCH_STARTS_WITH,
                    Constants.NOT_FOUND_RETURN_NEG_ONE);
        }
        return (first > -1);
    }

    String buildResult(String code, String msg) {
        return code + Constants.COLON + msg;
    }
}

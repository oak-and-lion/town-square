public class SquareWorkerMember extends SquareWorkerBase implements ISquareWorker {
    public SquareWorkerMember(IUtility utility, String command) {
        super(utility, command);
    }

    public SquareResponse doWork(ISquare square, String[] args) {
        return new SquareResponse(buildResult(Constants.OK_RESULT, getMembers(square, args)));
    }

    private String getMembers(ISquare square, String[] split) {
        // command arguments
        // 3 == member id
        if (split.length != 4) {
            return Constants.EMPTY_STRING;
        }

        String memberId = split[3];

        if (checkSquareAccess(square, memberId)) {
            return utility.readFile(utility.concatStrings(square.getSafeLowerName(), Constants.MEMBERS_FILE_EXT));
        }

        return Constants.EMPTY_STRING;
    }
}

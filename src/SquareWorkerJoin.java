public class SquareWorkerJoin extends SquareWorkerBase implements ISquareWorker {
    private IDialogController sampleController;

    public SquareWorkerJoin(IUtility utility, IDialogController sampleController, String command) {
        super(utility, command);
        this.sampleController = sampleController;
    }

    public SquareResponse doWork(ISquare square, String[] args) {
        return new SquareResponse(processJoinRequest(args, square));
    }

    private String processJoinRequest(String[] args, ISquare square) {
        // command arguments
        // 3 == desired name
        // 4 == member public key
        // 5 == ip address
        // 6 == port
        // 7 == unique id of member

        if (args.length == 8) {
            String file = utility.concatStrings(square.getSafeLowerName(), Constants.MEMBERS_FILE_EXT);
            String[] sameNames = utility.searchFile(file, args[3], Constants.SEARCH_STARTS_WITH);
            String[] sameIds = utility.searchFile(file, args[7], Constants.SEARCH_CONTAINS);
            boolean previousLeave = checkPreviousLeave(sameIds);
            processPreviousLeave(previousLeave, file, args);
            String registeredName = args[3];
            if (sameIds.length < 1 || previousLeave) {
                if (sameNames.length > 0) {
                    registeredName += utility.concatStrings(Constants.PERCENT, Integer.toString(sameNames.length));
                }

                String data = utility.concatStrings(args[4], Constants.DATA_SEPARATOR, args[5],
                        Constants.DATA_SEPARATOR, args[6], Constants.DATA_SEPARATOR, args[7]);

                return processCommand(registeredName, data, file, square);
            } else {
                return buildResult(Constants.ALREADY_REGISTERED_RESULT,
                        utility.concatStrings(Constants.ALREADY_REGISTERED_MESSAGE, Constants.COLON, Constants.DASH,
                                Constants.COLON, sampleController.getDefaultName(), Constants.UNDERSCORE,
                                square.getSafeLowerName(), Constants.COLON, Constants.NO_ROWS));
            }
        }

        return buildResult(Constants.MALFORMED_REQUEST_RESULT, Constants.MALFORMED_REQUEST_MESSAGE);
    }

    private boolean checkPreviousLeave(String[] sameIds) {
        boolean previousLeave = false;
        for (String sameId : sameIds) {
            if (sameId.startsWith(Constants.STAR)) {
                previousLeave = true;
                break;
            }
        }

        return previousLeave;
    }

    private void processPreviousLeave(boolean previousLeave, String file, String[] args) {
        if (previousLeave) {
            String[] members = utility.readFile(file).split(Constants.READ_FILE_DATA_SEPARATOR);
            StringBuilder result = new StringBuilder();
            for (String member : members) {
                if (!member.contains(args[7])) {
                    result.append(member);
                }
            }
            utility.writeFile(file, result.toString());
        }
    }

    private String processCommand(String data, String memberId, String file, ISquare square) {
        String result;

        if (square != null) {
            FileWriteResponse b;

            if (utility.checkFileExists(file)) {
                b = utility.appendToFile(file,
                        utility.concatStrings(Constants.NEWLINE, data, Constants.DATA_SEPARATOR, memberId));
            } else {
                b = utility.writeFile(file, utility.concatStrings(data, Constants.DATA_SEPARATOR, memberId));
            }

            if (b.isSuccessful()) {
                result = buildResult(Constants.OK_RESULT,
                        utility.concatStrings(Constants.ADDED_MESSAGE, Constants.COLON, data, Constants.COLON,
                                sampleController.getDefaultName(), Constants.UNDERSCORE, square.getSafeLowerName(),
                                Constants.COLON, Integer.toString(b.getLineCount())));
            } else {
                result = buildResult(Constants.INTERNAL_ERROR_RESULT, Constants.ADDING_MEMBER_MESSAGE);
            }
        } else {
            result = buildResult(Constants.UNKNOWN_SQUARE_RESULT, Constants.UNKNOWN_SQUARE_MESSAGE);
        }

        return result;
    }
}

public class SquareWorkerReadAlias extends SquareWorkerBase implements ISquareWorker {
    private IFactory factory;

    public SquareWorkerReadAlias(IUtility utility, String command, IFactory factory) {
        super(utility, command);
        this.factory = factory;
    }

    public SquareResponse doWork(ISquare square, String[] args) {
        String aliases = getAliases(square, args);
        String returnCode = Constants.OK_RESULT;
        if (aliases.equals(Constants.EMPTY_STRING)) {
            aliases = Constants.FORBIDDEN_MESSAGE;
            returnCode = Constants.FORBIDDEN_RESULT;
        }
        return new SquareResponse(buildResult(returnCode, aliases));
    }

    private String getAliases(ISquare square, String[] split) {
        // command arguments
        // 3 == member id
        if (split.length != 4) {
            return Constants.EMPTY_STRING;
        }

        String memberId = split[3];

        if (checkSquareAccess(square, memberId)) {
            String[] member = utility.searchFile(
                    utility.concatStrings(square.getSafeLowerName(), Constants.MEMBERS_FILE_EXT), memberId,
                    Constants.SEARCH_CONTAINS);
            String[] memberInfo = member[0].split(Constants.FILE_DATA_SEPARATOR);
            ISquareKeyPair tempKeys = factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility);
            tempKeys.setPublicKeyFromBase64(memberInfo[1]);
            String password = utility.generateRandomString(Constants.ENCRYPTION_KEY_LENGTH);
            StringBuilder temp = new StringBuilder();
            temp.append(utility.encrypt(
                    utility.readFile(utility.concatStrings(square.getSafeLowerName(), Constants.ALIAS_FILE_EXT)),
                    password));
            return utility.concatStrings(tempKeys.encryptToBase64(password), Constants.COMMAND_DATA_SEPARATOR,
                    temp.toString());
        }

        return Constants.EMPTY_STRING;
    }
}

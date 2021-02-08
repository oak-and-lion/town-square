import java.util.ArrayList;

public class SquareWorkerRead extends SquareWorkerBase implements ISquareWorker {
    private IFactory factory;
    private ILogIt logger;

    public SquareWorkerRead(IUtility utility, IFactory factory, ILogIt logger, String command) {
        super(utility, command);
        this.factory = factory;
        this.logger = logger;
    }

    public SquareResponse doWork(ISquare square, String[] args) {
        return new SquareResponse(buildResult(Constants.OK_RESULT, getPosts(square, args)));
    }

    private String getPosts(ISquare square, String[] split) {
        // command arguments
        // 3 == last known timestamp
        // 4 == requesting member id

        if (split.length != 5) {
            return Constants.EMPTY_STRING;
        }

        String start = split[3];
        String memberId = split[4];

        if (checkSquareAccess(square, memberId)) {
            String file = square.getSafeLowerName() + Constants.POSTS_FILE_EXT;
            String memberFile = square.getSafeLowerName() + Constants.MEMBERS_FILE_EXT;
            boolean getEntireFile = Long.valueOf(start) > -1 ? Constants.NOT_FOUND_RETURN_ZERO
                    : !Constants.NOT_FOUND_RETURN_ZERO;
            if (getEntireFile != Constants.NOT_FOUND_RETURN_ZERO) {
                logger.logInfo("Need whole file");
            }
            int firstRow = utility.findFirstOccurence(file, start, Constants.SEARCH_STARTS_WITH, getEntireFile);
            String posts = utility.readFile(file, firstRow);

            String[] members = utility.readFile(memberFile).split(Constants.COMMAND_DATA_SEPARATOR);

            ArrayList<String> memberIds = new ArrayList<>();
            ArrayList<String> memberNames = new ArrayList<>();

            for (int x = 0; x < members.length; x++) {
                String[] data = members[x].split(Constants.DATA_SEPARATOR);
                if (data[4].equals(split[4])) {
                    memberNames.add(data[0]);
                    memberIds.add(data[1]);
                }
            }

            if (!memberNames.isEmpty()) {
                ISquareKeyPair tempKeys = factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility);
                tempKeys.setPublicKeyFromBase64(memberIds.get(0));
                String password = utility.generateRandomString(Constants.ENCRYPTION_KEY_LENGTH);
                StringBuilder temp = new StringBuilder();
                temp.append(utility.encrypt(posts, password));
                posts = tempKeys.encryptToBase64(password) + Constants.COMMAND_DATA_SEPARATOR + temp.toString();
                return posts;
            }

            return Constants.EMPTY_STRING;
        }

        return Constants.EMPTY_STRING;
    }
}

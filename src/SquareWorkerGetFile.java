import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class SquareWorkerGetFile extends SquareWorkerBase implements ISquareWorker {
    private IFactory factory;
    private ILogIt errorLogger;

    public SquareWorkerGetFile(IUtility utility, String command, IFactory factory) {
        super(utility, command);
        this.factory = factory;
    }

    public SquareResponse doWork(ISquare square, String[] args) {
        this.errorLogger = this.factory.createLogger(Constants.ERROR_LOGGER, Constants.ERROR_LOG_FILE, utility, square.getSampleController());
        return new SquareResponse(buildResult(Constants.OK_RESULT, processFileGetMessage(square, args)));
    }

    private String processFileGetMessage(ISquare square, String[] split) {
        // command arguments
        // 3 == file name
        // 4 == requesting member id

        if (split.length != 5) {
            return Constants.EMPTY_STRING;
        }

        String fileRequest = split[3].trim().toLowerCase();
        if (!utility.checkFileExists(fileRequest)) {
            return Constants.EMPTY_STRING;
        }
        
        if (fileRequest.endsWith(Constants.KEY_FILE_EXT) || fileRequest.endsWith(Constants.BLOCK_FILE_EXT)
                || fileRequest.endsWith(Constants.MEMBERS_FILE_EXT)
                || fileRequest.endsWith(Constants.POSTS_FILE_EXT) || fileRequest.endsWith(Constants.LOG_FILE_EXT)
                || fileRequest.endsWith(Constants.ID_FILE_EXT) || fileRequest.endsWith(Constants.TXT_FILE_EXT)
                || fileRequest.endsWith(Constants.SH_FILE_EXT) || fileRequest.endsWith(Constants.BAT_FILE_EXT)
                || fileRequest.endsWith(Constants.SQUARE_FILE_EXT) || fileRequest.endsWith(Constants.CLONE_FILE_EXT)) {
            return Constants.GO_AWAY;
        }

        String result = Constants.EMPTY_STRING;
        String memberFile = utility.concatStrings(square.getSafeLowerName(), Constants.MEMBERS_FILE_EXT);

        try (InputStream stream = new FileInputStream(split[3])) {
            result = utility.convertToBase64(stream.readAllBytes());

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

            ISquareKeyPair tempKeys = factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility);
            tempKeys.setPublicKeyFromBase64(memberIds.get(0));
            String password = utility.generateRandomString(Constants.ENCRYPTION_KEY_LENGTH);
            StringBuilder temp = new StringBuilder();
            temp.append(utility.encrypt(result, password));
            result = utility.concatStrings(tempKeys.encryptToBase64(password), Constants.COMMAND_DATA_SEPARATOR,
                    temp.toString());
        } catch (IOException ioe) {
            errorLogger.logInfo(ioe.getMessage());
        } catch (Exception e) {
            errorLogger.logInfo(e.getMessage());
        }

        return result;
    }
}

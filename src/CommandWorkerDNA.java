import java.util.ArrayList;
import java.util.List;

public class CommandWorkerDNA extends CommandWorkerBase implements ICommandWorker {
    public CommandWorkerDNA(IUtility utility, ISquare square, IDialogController parent) {
        super(utility, square, parent);
    }

    public List<BooleanString> doWork(String commandArgs) {
        ArrayList<BooleanString> result = new ArrayList<>();
        result.add(new BooleanString(createDNA(commandArgs), Constants.EMPTY_STRING));
        return result;
    }

    private boolean createDNA(String password) {
        StringBuilder tempPass = new StringBuilder(password);
        if (password.length() < Constants.ENCRYPTION_KEY_LENGTH) {
            for (int x = password.length(); x < Constants.ENCRYPTION_KEY_LENGTH; x++) {
                tempPass.append(Constants.UNDERSCORE);
            }
        }
        String data = utility.encrypt(Constants.CLONE_CHALLENGE, tempPass.toString());
        FileWriteResponse result = utility.writeFile(utility.concatStrings(square.getSafeLowerName(), Constants.DNA_FILE_EXT), data);
        return result.isSuccessful();
    }
}

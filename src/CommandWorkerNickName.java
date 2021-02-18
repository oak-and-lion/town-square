import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;

public class CommandWorkerNickName extends CommandWorkerBase implements ICommandWorker {
    ILogIt errorLogger;

    public CommandWorkerNickName(IUtility utility, ISquare square, IDialogController parent) {
        super(utility, square, parent);
        this.errorLogger = square.getFactory().createLogger(Constants.ERROR_LOGGER, Constants.ERROR_LOG_FILE, utility, this.parent);
    }

    public List<BooleanString> doWork(String commandArgs) {
        String file = utility.concatStrings(square.getSafeLowerName(), Constants.NICKNAME_FILE_EXT);

        boolean result = false;
        String oldName = square.getName();

        if (commandArgs.equals(Constants.DELETE_COMMAND)) {
            result = utility.deleteFile(file);
            
        } else {
            FileWriteResponse response = utility.writeFile(file, commandArgs);
            result = response.isSuccessful();
        }


        if (result && parent.isGui()) {
            try {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        parent.setTabName(square, oldName, square.getName());
                    }
                });
            } catch (Exception e) {
                errorLogger.logInfo(e.getMessage());
            }
        }

        ArrayList<BooleanString> list = new ArrayList<>();
        list.add(new BooleanString(result, file));
        return list;
    }
}

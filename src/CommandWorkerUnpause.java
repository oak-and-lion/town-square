import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;

public class CommandWorkerUnpause extends CommandWorkerBase implements ICommandWorker {
    public CommandWorkerUnpause(IUtility utility, ISquare square, IDialogController parent) {
        super(utility, square, parent);
    }

    public List<BooleanString> doWork(String commandArgs) {
        ArrayList<BooleanString> result = new ArrayList<>();
        unPauseSquare(square);
        result.add(new BooleanString(true, Constants.EMPTY_STRING));
        return result;
    }

    public boolean unPauseSquare(ISquare square) {
        if (square == null) {
            return false;
        }

        boolean result = utility.deleteFile(utility.concatStrings(square.getSafeLowerName(), Constants.PAUSE_FILE_EXT));

        if (result && parent.isGui()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    parent.updatePauseNotification(square, false);
                }
            });
        }

        return result;
    }
}

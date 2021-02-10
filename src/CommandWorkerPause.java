import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;

public class CommandWorkerPause extends CommandWorkerBase implements ICommandWorker {
    public CommandWorkerPause(IUtility utility, ISquare square, IDialogController parent) {
        super(utility, square, parent);
    }

    public List<BooleanString> doWork(String commandArgs) {
        ArrayList<BooleanString> list = new ArrayList<>();
        list.add(new BooleanString(pauseSquare(square), Constants.EMPTY_STRING));
        return list;
    }

    @Override
    boolean pauseSquare(ISquare square) {
        if (square == null) {
            return false;
        }

        FileWriteResponse result = utility.writeFile(
                utility.concatStrings(square.getSafeLowerName(), Constants.PAUSE_FILE_EXT),
                Constants.PAUSE_FILE_CONTENTS);

        if (result.isSuccessful() && parent.isGui()) {
            try {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        parent.updatePauseNotification(square, true);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result.isSuccessful();
    }
}

import javafx.application.Platform;

public class CommandWorkerPause extends CommandWorkerBase implements ICommandWorker {
    public CommandWorkerPause(IUtility utility, ISquare square, IDialogController parent) {
        super(utility, square, parent);
    }

    public BooleanString doWork(String commandArgs) {
        return new BooleanString(pauseSquare(square), Constants.EMPTY_STRING);
    }

    @Override
    boolean pauseSquare(ISquare square) {
        if (square == null) {
            return false;
        }

        FileWriteResponse result = utility.writeFile(square.getSafeLowerName() + Constants.PAUSE_FILE_EXT,
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

import javafx.application.Platform;

public class CommandWorkerNickName extends CommandWorkerBase implements ICommandWorker {
    public CommandWorkerNickName(IUtility utility, ISquare square, IDialogController parent) {
        super(utility, square, parent);
    }

    public BooleanString doWork(String commandArgs) {
        String file = square.getSafeLowerName() + Constants.NICKNAME_FILE_EXT;

        boolean result = false;
        String oldName = square.getName();

        if (commandArgs.equals("delete")) {
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
                e.printStackTrace();
            }
        }

        return new BooleanString(result, file);
    }
}

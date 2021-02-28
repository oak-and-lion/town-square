import java.util.Arrays;

public class SyncClone extends Thread implements ISyncClone {
    private IUtility utility;

    public SyncClone(IUtility utility, IApp parent, ILogIt logger) {
        this.utility = utility;
    }

    @Override
    public void run() {
        while (true) {
            // not sure what to do here
            // basically, need to go to each cloned member and tell them the squares we already have
            // then that member returns the squares that we don't have

            try {
                Thread.sleep(Constants.SYNC_CLONE_WAIT);
            } catch (InterruptedException ie) {
                utility.logError(
                        utility.concatStrings(ie.getMessage(), Constants.NEWLINE, Arrays.toString(ie.getStackTrace())));
                Thread.currentThread().interrupt();
            }
        }
    }
}
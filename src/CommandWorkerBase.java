public class CommandWorkerBase {
    IUtility utility;
    ISquare square;
    IDialogController parent;

    public CommandWorkerBase(IUtility utility, ISquare square, IDialogController parent) {
        this.utility = utility;
        this.square = square;
        this.parent = parent;
    }

    boolean pauseSquare(ISquare square) {
        // override as needed
        return false;
    }
}

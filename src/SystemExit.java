public class SystemExit implements ISystemExit {
    private IApp parent;
    public void setParent(IApp parent) {
        this.parent = parent;
    }
    public void handleExit(int returnCode) {
        parent.close(returnCode);
        System.exit(returnCode);
    }
}

public interface ISystemExit {
    void handleExit(int returnCode);
    void setParent(IApp parent);
}

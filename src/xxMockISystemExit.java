public class xxMockISystemExit implements ISystemExit {
    public void handleExit(int returnCode) {
        IUtility utility = Factory.createUtility(Constants.BASE_UTILITY);
        utility.writeFile("systemExit.txt", "exiting");
    }
}

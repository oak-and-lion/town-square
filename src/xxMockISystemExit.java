public class xxMockISystemExit implements ISystemExit {
    public void handleExit(int returnCode) {
        IFactory factory = new Factory();
        IUtility utility = factory.createUtility(Constants.BASE_UTILITY, new xxMockIDialogController());
        utility.writeFile("systemExit.txt", "exiting");
    }
    public void setParent(IApp parent){
        // not needed in the mock
    }
}

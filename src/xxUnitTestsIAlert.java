public class xxUnitTestsIAlert extends xxUnitTestsBaseClass {

    private static final String TOWN_SQUARE_LOCK_FILE = "town_square.lock";
    private static final String ALERT_FILE = "alert.txt";
    private static final String SYSTEM_EXIT_FILE = "systemExit.txt";

    public xxUnitTestsIAlert(String suiteName) {
        super(suiteName);
    }

    public void go() {
        testIAlertPass();

        finish();
    }

    private void testIAlertPass() {
        IUtility utility = Utility.create();
        utility.writeFile(TOWN_SQUARE_LOCK_FILE, "lock");
        App.execute(new xxMockIAlertBox(), new xxMockISystemExit());
        utility.deleteFile(TOWN_SQUARE_LOCK_FILE);

        checkEquals(utility.checkFileExists(ALERT_FILE), true, "testIAlertPass");
        checkEquals(utility.checkFileExists(SYSTEM_EXIT_FILE), true, "testIAlertPass");

        utility.deleteFile(ALERT_FILE);
        utility.deleteFile(TOWN_SQUARE_LOCK_FILE);
        utility.deleteFile(SYSTEM_EXIT_FILE);
    }
}

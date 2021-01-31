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

    @TestMethod
    private void testIAlertPass() {
        final String METHOD_NAME = "testIAlertPass";
        IUtility utility = Utility.create();
        utility.writeFile(TOWN_SQUARE_LOCK_FILE, "lock");
        App.execute(new xxMockIAlertBox(), new xxMockISystemExit(), new Factory());
        utility.deleteFile(TOWN_SQUARE_LOCK_FILE);

        // these files are written by the mock alert box object from the above line
        checkEquals(utility.checkFileExists(ALERT_FILE), true, METHOD_NAME);
        checkEquals(utility.checkFileExists(SYSTEM_EXIT_FILE), true, METHOD_NAME);

        boolean result = utility.deleteFile(ALERT_FILE);
        checkEquals(result, true, METHOD_NAME);
        utility.deleteFile(TOWN_SQUARE_LOCK_FILE);
        checkEquals(result, true, METHOD_NAME);
        utility.deleteFile(SYSTEM_EXIT_FILE);
        checkEquals(result, true, METHOD_NAME);
    }
}

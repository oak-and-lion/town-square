public class xxUnitTestsUtility extends xxUnitTestsBaseClass {
    private static final String TEST_UTILITY_FILE = "test-utility-file.txt";
    private static final String INVALID_TEST_UTILITY_FILE = "this-file-does-not-exist.what";
    private static final String ACTUAL_VALUE = "  actual value == ";

    public xxUnitTestsUtility(String suiteName) {
        super(suiteName);
    }

    public void go() {
        readLastLineOfFilePass();
        readLastLineOfInvalidFilePass();

        countLinesOfFilePass();
        countLinesOfInvalidFilePass();

        findFirstOccurrencePass();
        findFirstOccurrenceFail();
        findFirstOccurrenceNonZeroFail();
        findFirstOccurrenceInvalidFileFail();

        decodeBase64NullStringPass();
        decodeBase64ValidStringPass();
        decodeBase64InvalidStringFail();
        decodeBase64NullStringPass();

        finish();
    }

    private void readLastLineOfFilePass() {
        final String METHOD_NAME = "readLastLineOfFilePass";

        IUtility utility = Utility.create();

        String result = utility.readLastLineOfFile(TEST_UTILITY_FILE);

        checkEquals(result, "last line", METHOD_NAME + ACTUAL_VALUE + result);
    }

    private void readLastLineOfInvalidFilePass() {
        final String METHOD_NAME = "readLastLineOfInvalidFilePass";

        IUtility utility = Utility.create();

        String result = utility.readLastLineOfFile(INVALID_TEST_UTILITY_FILE);

        checkEquals(result, Constants.EMPTY_STRING, METHOD_NAME + ACTUAL_VALUE + result);
    }

    private void countLinesOfFilePass() {
        final String METHOD_NAME = "countLinesOfFilePass";

        IUtility utility = Utility.create();

        int result = utility.countLinesInFile(TEST_UTILITY_FILE);

        checkEquals(result, 6, METHOD_NAME + ACTUAL_VALUE + result);
    }

    private void countLinesOfInvalidFilePass() {
        final String METHOD_NAME = "countLinesOfInvalidFilePass";

        IUtility utility = Utility.create();

        int result = utility.countLinesInFile(INVALID_TEST_UTILITY_FILE);

        checkEquals(result, -1, METHOD_NAME + ACTUAL_VALUE + result);
    }

    private void findFirstOccurrencePass() {
        final String METHOD_NAME = "findFirstOccurrencePass";

        IUtility utility = Utility.create();

        int result = utility.findFirstOccurence(TEST_UTILITY_FILE, "is", true, false);

        checkEquals(result, 1, METHOD_NAME + ACTUAL_VALUE + result);
    }

    private void findFirstOccurrenceFail() {
        final String METHOD_NAME = "findFirstOccurrenceFail";

        IUtility utility = Utility.create();

        int result = utility.findFirstOccurence(TEST_UTILITY_FILE, "isa", true, false);

        checkEquals(result, 6, METHOD_NAME + ACTUAL_VALUE + result);
    }

    private void findFirstOccurrenceNonZeroFail() {
        final String METHOD_NAME = "findFirstOccurrenceNonZeroFail";

        IUtility utility = Utility.create();

        int result = utility.findFirstOccurence(TEST_UTILITY_FILE, "isa", true, true);

        checkEquals(result, -1, METHOD_NAME + ACTUAL_VALUE + result);
    }

    private void findFirstOccurrenceInvalidFileFail() {
        final String METHOD_NAME = "findFirstOccurrenceInvalidFileFail";

        IUtility utility = Utility.create();

        int result = utility.findFirstOccurence(INVALID_TEST_UTILITY_FILE, "is", true, true);

        checkEquals(result, -1, METHOD_NAME + ACTUAL_VALUE + result);
    }

    private void decodeBase64NullStringPass() {
        final String METHOD_NAME = "decodeBase64NullStringPass";

        IUtility utility = Utility.create();

        byte[] result = utility.convertFromBase64("");

        checkEquals(result.length, 0, METHOD_NAME + ACTUAL_VALUE + result);
    }

    private void decodeBase64EmptyStringPass() {
        final String METHOD_NAME = "decodeBase64NullStringPass";

        IUtility utility = Utility.create();

        byte[] result = utility.convertFromBase64(null);

        checkEquals(result.length, 0, METHOD_NAME + ACTUAL_VALUE + result);
    }

    private void decodeBase64ValidStringPass() {
        final String METHOD_NAME = "decodeBase64ValidStringPass";

        IUtility utility = Utility.create();

        byte[] result = utility.convertFromBase64("dGVzdCBkYXRh");

        checkEquals(result.length, 9, METHOD_NAME + ACTUAL_VALUE + result);
        checkEquals(new String(result), "test data", METHOD_NAME + ACTUAL_VALUE + new String(result));
    }

    private void decodeBase64InvalidStringFail() {
        final String METHOD_NAME = "decodeBase64InvalidStringFail";

        IUtility utility = Utility.create();

        byte[] result = utility.convertFromBase64("dGVzdCBkYXRhr");

        checkEquals(result.length, 0, METHOD_NAME + ACTUAL_VALUE + new String(result));
        checkEquals(new String(result), "", METHOD_NAME + ACTUAL_VALUE + new String(result));
    }
}

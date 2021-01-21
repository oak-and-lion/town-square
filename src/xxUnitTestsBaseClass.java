import java.util.ArrayList;

public class xxUnitTestsBaseClass {
    private int errorCount;
    private ArrayList<String> errorTests;
    private String suiteName;

    public xxUnitTestsBaseClass(String testSuite) {
        initialize();
        setup(testSuite);
    }

    private void initialize() {
        errorCount = 0;
        errorTests = new ArrayList<String>();
    }

    void setup(String testSuite) {
        suiteName = testSuite;
    }

    private void error(String methodName) {
        errorCount++;
        errorTests.add(methodName);
    }

    void forceError(String methodName) {
        error(methodName);
    }

    void finish() {
        ILogIt logger = LogItConsole.create();
        logger.logInfo("[" + suiteName + "] Results");
        logger.logInfo("  Errors: " + Integer.toString(errorCount));
        if (errorCount > 0) {
            logger.logInfo("  Methods in error:");
            for (String method : errorTests) {
                logger.logInfo("    " + method);
            }
        }
        logger.logInfo("---------------");
    }

    void checkEquals(Object actual, Object expected, String methodName) {
        if (actual != expected) {
            error(methodName);
        }
    }

    void checkNotEquals(Object actual, Object expected, String methodName) {
        if (actual == expected) {
            error(methodName);
        }
    }

    void checkClass(Class<?> actual, Class<?> expected, String methodName) {
        if (actual != expected) {
            error(methodName);
        }
    }
}
import java.lang.reflect.Method;
import java.util.ArrayList;

public class xxUnitTestsBaseClass {
    private int errorCount;
    private ArrayList<String> errorTests;
    private String suiteName;
    private int totalTests;

    public xxUnitTestsBaseClass(String testSuite) {
        initialize();
        setup(testSuite);
    }

    public int getTotalTests() {
        return totalTests;
    }

    public int getTotalFailed() {
        return errorCount;
    }

    private void initialize() {
        errorCount = 0;
        errorTests = new ArrayList<String>();

        totalTests = 0;
        Class<?> clazz = this.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(TestMethod.class)) {
                totalTests++;
            }
        }
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
        logger.logInfo("Total tests: " + totalTests);
        logger.logInfo("  Errors: " + Integer.toString(errorCount));
        if (errorCount > 0) {
            logger.logInfo("  Methods in error:");
            for (String method : errorTests) {
                logger.logInfo("    " + method);
            }
        }
        logger.logInfo("---------------");
    }

    void checkEquals(String actual, String expected, String methodName) {
        if (!actual.equals(expected)) {
            error(methodName);
        }
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
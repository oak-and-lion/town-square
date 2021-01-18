import java.util.ArrayList;

public class _FactoryUnitTests {
    private static int errorCount;
    private static ArrayList<String> errorTests;
    
    public static void main(String[] args) {
        errorCount = 0;
        errorTests = new ArrayList<String>();
        testCreateSquareKeyPairPass();
        testCreateSquareKeyPairFail();

        ILogIt logger = LogItConsole.create();
        logger.logInfo("Errors: " + Integer.toString(errorCount));
        for(String method : errorTests) {
            logger.logInfo("Method w/ error: " + method);
        }
    }

    private static void error(String methodName) {
        errorCount++;
        errorTests.add(methodName);
    }

    public static void testCreateSquareKeyPairPass() {
        ISquareKeyPair test = Factory.createSquareKeyPair(Constants.BASE_SQUARE_KEY_PAIR);

        if (test == null) {
            error("testCreateSquareKeyPairPass");
        }
    }

    public static void testCreateSquareKeyPairFail() {
        ISquareKeyPair test = Factory.createSquareKeyPair(1);

        if (test != null) {
            error("testCreateSquareKeyPairFail");
        }
    }
}

public class xxUnitTestRun {
    public static void main(String[] args) {
        xxUnitTestsCommandController commandControllerUnitTests = new xxUnitTestsCommandController("CommandController");

        commandControllerUnitTests.go();

        xxUnitTestsFactory factoryUnitTests = new xxUnitTestsFactory("Factory");

        factoryUnitTests.go();

        xxUnitTestsIAlert alertUnitTests = new xxUnitTestsIAlert("IAlert");

        alertUnitTests.go();

        xxUnitTestsUtility utilityUnitTests = new xxUnitTestsUtility("Utility");

        utilityUnitTests.go();

        printFinalResults(alertUnitTests.getTotalTests() + factoryUnitTests.getTotalTests()
                + commandControllerUnitTests.getTotalTests() + utilityUnitTests.getTotalTests(),
                alertUnitTests.getTotalFailed() + factoryUnitTests.getTotalFailed()
                + commandControllerUnitTests.getTotalFailed() + utilityUnitTests.getTotalFailed());
    }

    private static void printFinalResults(int totalTests, int totalErrors) {
        ILogIt logger = LogItConsole.create(new xxMockIDialogController());

        logger.logInfo("Total Tests Executed: " + convertToString(totalTests));
        logger.logInfo("Total Failed Tests: " + convertToString(totalErrors));
    }

    private static String convertToString(int i) {
        return Integer.toString(i);
    }
}

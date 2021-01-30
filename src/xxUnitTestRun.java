public class xxUnitTestRun {
    public static void main(String[] args) {
        xxUnitTestsIAlert alertUnitTests = new xxUnitTestsIAlert("IAlert");

        alertUnitTests.go();

        xxUnitTestsFactory factoryUnitTests = new xxUnitTestsFactory("Factory");

        factoryUnitTests.go();

        xxUnitTestsCommandController commandControllerUnitTests = new xxUnitTestsCommandController("CommandController");

        commandControllerUnitTests.go();
    }
}

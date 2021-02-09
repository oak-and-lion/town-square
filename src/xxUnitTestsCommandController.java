public class xxUnitTestsCommandController extends xxUnitTestsBaseClass {
    private static final String WRONG_RESULT_VALUE_MESSAGE = "Wrong result value ";
    private static final String WRONG_RESULT_LENGTH_MESSAGE = "Wrong result length ";

    private IFactory factory;

    public xxUnitTestsCommandController(String suiteName) {
        super(suiteName);
        factory = new Factory();
    }

    public void go() {
        testProcessCommandInvalidPrefixFail();
        testProcessCommandInvalidCommandFail();
        testProcessCommandValidBlockPass();
        testProcessCommandInvalidBlockPass();

        finish();
    }

    @TestMethod
    private void testProcessCommandInvalidPrefixFail() {
        final String METHOD_NAME = "testProcessCommandInvalidPrefixFail";
        final String INVALID_COMMAND_PREFIX = "-command";
        ICommandController commandController = factory.createCommandController(1, new xxMockIUtility(), new xxMockIDialogController());

        BooleanString[] result = commandController.processCommand(INVALID_COMMAND_PREFIX, new xxMockSquare());

        checkEquals(result[0].getBoolean(), false, WRONG_RESULT_VALUE_MESSAGE + METHOD_NAME);
        checkEquals(result.length, 1, WRONG_RESULT_LENGTH_MESSAGE + METHOD_NAME);
    }

    @TestMethod
    private void testProcessCommandInvalidCommandFail() {
        final String METHOD_NAME = "testProcessCommandInvalidCommandFail";
        final String INVALID_COMMAND = "/command";
        ICommandController commandController = factory.createCommandController(1, new xxMockIUtility(), new xxMockIDialogController());

        BooleanString[] result = commandController.processCommand(INVALID_COMMAND, new xxMockSquare());

        checkEquals(result[0].getBoolean(), false, WRONG_RESULT_VALUE_MESSAGE + METHOD_NAME);
        checkEquals(result.length, 1, WRONG_RESULT_LENGTH_MESSAGE + METHOD_NAME);
    }

    @TestMethod
    private void testProcessCommandValidBlockPass() {
        final String METHOD_NAME = "testProcessCommandValidBlockPass";
        final String COMMAND = "/block searchFound";
        ICommandController commandController = factory.createCommandController(1, new xxMockIUtility(), new xxMockIDialogController());

        BooleanString[] result = commandController.processCommand(COMMAND, new xxMockSquare());

        checkEquals(result[0].getBoolean(), true, WRONG_RESULT_VALUE_MESSAGE + METHOD_NAME);
        checkEquals(result.length, 1, WRONG_RESULT_LENGTH_MESSAGE + METHOD_NAME);
    }

    @TestMethod
    private void testProcessCommandInvalidBlockPass() {
        final String METHOD_NAME = "testProcessCommandInvalidBlockPass";
        final String COMMAND = "/block searchNotFound";
        ICommandController commandController = factory.createCommandController(1, new xxMockIUtility(), new xxMockIDialogController());

        BooleanString[] result = commandController.processCommand(COMMAND, new xxMockSquare());

        checkEquals(result[0].getBoolean(), false, WRONG_RESULT_VALUE_MESSAGE + METHOD_NAME);
        checkEquals(result.length, 1, WRONG_RESULT_LENGTH_MESSAGE + METHOD_NAME);
    }
}

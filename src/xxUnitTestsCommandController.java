public class xxUnitTestsCommandController extends xxUnitTestsBaseClass {
    private static final String WRONG_RESULT_VALUE_MESSAGE = "Wrong result value ";
    private static final String WRONG_RESULT_LENGTH_MESSAGE = "Wrong result length ";

    public xxUnitTestsCommandController(String suiteName) {
        super(suiteName);
    }

    public void go() {
        testBlockCommandPass();
        testBlockCommandFail();
        testBlockCommandNullFail();
        testBlockCommandEmptyFail();
        testProcessCommandInvalidPrefixFail();
        testProcessCommandInvalidCommandFail();
        testProcessCommandValidBlockPass();
        testProcessCommandInvalidBlockPass();

        finish();
    }

    private void testBlockCommandPass() {
        final String METHOD_NAME = "testBlockCommandPass";
        ICommandController commandController = Factory.createCommandController(1, new xxMockIUtility());

        boolean result = commandController.blockUser("searchFound", new xxMockSquare());

        checkEquals(result, true, METHOD_NAME);
    }

    private void testBlockCommandFail() {
        final String METHOD_NAME = "testBlockCommandFail";
        ICommandController commandController = Factory.createCommandController(1, new xxMockIUtility());

        boolean result = commandController.blockUser("searchNotFound", new xxMockSquare());

        checkEquals(result, false, METHOD_NAME);
    }

    private void testBlockCommandNullFail() {
        final String METHOD_NAME = "testBlockCommandNullFail";
        ICommandController commandController = Factory.createCommandController(1, new xxMockIUtility());

        boolean result = commandController.blockUser(null, new xxMockSquare());

        checkEquals(result, false, METHOD_NAME);
    }

    private void testBlockCommandEmptyFail() {
        final String METHOD_NAME = "testBlockCommandEmptyFail";
        ICommandController commandController = Factory.createCommandController(1, new xxMockIUtility());

        boolean result = commandController.blockUser("", new xxMockSquare());

        checkEquals(result, false, METHOD_NAME);
    }

    private void testProcessCommandInvalidPrefixFail() {
        final String METHOD_NAME = "testProcessCommandInvalidPrefixFail";
        final String INVALID_COMMAND_PREFIX = "-command";
        ICommandController commandController = Factory.createCommandController(1, new xxMockIUtility());

        Boolean[] result = commandController.processCommand(INVALID_COMMAND_PREFIX, new xxMockSquare());

        checkEquals(result[0], false, WRONG_RESULT_VALUE_MESSAGE + METHOD_NAME);
        checkEquals(result.length, 1, WRONG_RESULT_LENGTH_MESSAGE + METHOD_NAME);
    }

    private void testProcessCommandInvalidCommandFail() {
        final String METHOD_NAME = "testProcessCommandInvalidCommandFail";
        final String INVALID_COMMAND = "/command";
        ICommandController commandController = Factory.createCommandController(1, new xxMockIUtility());

        Boolean[] result = commandController.processCommand(INVALID_COMMAND, new xxMockSquare());

        checkEquals(result[0], false, WRONG_RESULT_VALUE_MESSAGE + METHOD_NAME);
        checkEquals(result.length, 1, WRONG_RESULT_LENGTH_MESSAGE + METHOD_NAME);
    }

    private void testProcessCommandValidBlockPass() {
        final String METHOD_NAME = "testProcessCommandValidBlockPass";
        final String COMMAND = "/block searchFound";
        ICommandController commandController = Factory.createCommandController(1, new xxMockIUtility());

        Boolean[] result = commandController.processCommand(COMMAND, new xxMockSquare());

        checkEquals(result[0], true, WRONG_RESULT_VALUE_MESSAGE + METHOD_NAME);
        checkEquals(result.length, 1, WRONG_RESULT_LENGTH_MESSAGE + METHOD_NAME);
    }

    private void testProcessCommandInvalidBlockPass() {
        final String METHOD_NAME = "testProcessCommandInvalidBlockPass";
        final String COMMAND = "/block searchNotFound";
        ICommandController commandController = Factory.createCommandController(1, new xxMockIUtility());

        Boolean[] result = commandController.processCommand(COMMAND, new xxMockSquare());

        checkEquals(result[0], false, WRONG_RESULT_VALUE_MESSAGE + METHOD_NAME);
        checkEquals(result.length, 1, WRONG_RESULT_LENGTH_MESSAGE + METHOD_NAME);
    }
}
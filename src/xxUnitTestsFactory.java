import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class xxUnitTestsFactory extends xxUnitTestsBaseClass {
    public xxUnitTestsFactory(String suiteName) {
        super(suiteName);
    }

    public void go() {
        testCreateSquareKeyPairPass();
        testCreateSquareKeyPairUtilityPass();
        testCreateSquareKeyPairKeyPairPass();
        testCreateSquareKeyPairFail();
        testCreateCryptoUtilsPass();
        testCreateCryptoUtilsFail();
        testCreateSquareControllerPass();
        testCreateSquareControllerFail();
        testCreateClientPass();
        testCreateClientFail();
        testCreateUtilityPass();
        testCreateUtilityFail();

        finish();
    }

    @TestMethod
    public void testCreateSquareKeyPairPass() {
        ISquareKeyPair test = Factory.createSquareKeyPair(Constants.BASE_SQUARE_KEY_PAIR);

        checkNotEquals(test, null, "testCreateSquareKeyPairPass");
        checkClass(test.getClass(), SquareKeyPair.class, "testCreateSquareKeyPairPass");
    }

    @TestMethod
    public void testCreateSquareKeyPairUtilityPass() {
        ISquareKeyPair test = Factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, new xxMockIUtility());

        checkNotEquals(test, null, "testCreateSquareKeyPairUtilityPass");
        checkClass(test.getClass(), SquareKeyPair.class, "testCreateSquareKeyPairUtiltyPass");
    }

    @TestMethod
    public void testCreateSquareKeyPairKeyPairPass() {
        final String METHOD_NAME = "testCreateSquareKeyPairKeyPairPass";
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance(Constants.RSA);
        } catch (NoSuchAlgorithmException e) {
            forceError(METHOD_NAME);
            return;
        }
        if (kpg != null) {
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();

            ISquareKeyPair test = Factory.createSquareKeyPair(Constants.KEYS_SQUARE_KEY_PAIR, kp.getPublic(),
                    kp.getPrivate());

            checkNotEquals(test, null, METHOD_NAME);
            checkClass(test.getClass(), SquareKeyPair.class, METHOD_NAME);
        } else {
            forceError(METHOD_NAME);
        }
    }

    @TestMethod
    public void testCreateSquareKeyPairFail() {
        ISquareKeyPair test = Factory.createSquareKeyPair(Constants.NULL_OBJECT_TYPE, new xxMockIUtility());

        checkEquals(test, null, "testCreateSquareKeyPairFail");
    }

    @TestMethod
    public void testCreateCryptoUtilsPass() {
        ICryptoUtils test = Factory.createCryptoUtils(Constants.BASE_CRYPTO_UTILS);

        checkNotEquals(test, null, "testCreateCryptoUtilsPass");
        checkClass(test.getClass(), CryptoUtils.class, "testCreateCryptoUtilsPass");
    }

    @TestMethod
    public void testCreateCryptoUtilsFail() {
        ICryptoUtils test = Factory.createCryptoUtils(Constants.NULL_OBJECT_TYPE);

        checkEquals(test, null, "testCreateCryptoUtilsFail");
    }

    @TestMethod
    public void testCreateSquareControllerPass() {
        ISquareController test = Factory.createSquareController(Constants.BASE_SQUARE_CONTROLLER, new xxMockIUtility(),
                new xxMockIDialogController(), new xxMockILogIt(), new xxMockISquareKeyPair(new xxMockIUtility()));

        checkNotEquals(test, null, "testCreateSquareControllerPass");
        checkClass(test.getClass(), SquareController.class, "testCreateSquareControllerPass");
    }

    @TestMethod
    public void testCreateSquareControllerFail() {
        ISquareController test = Factory.createSquareController(Constants.NULL_OBJECT_TYPE, new xxMockIUtility(),
                new xxMockIDialogController(), new xxMockILogIt(), new xxMockISquareKeyPair(new xxMockIUtility()));

        checkEquals(test, null, "testCreateSquareControllerFail");
    }

    @TestMethod
    public void testCreateServerPass() {
        final String METHOD_NAME = "testCreateServerPass";
        IServer test = Factory.createServer(Constants.BASE_SQUARE_CONTROLLER, 1, new xxMockSquareController(), new xxMockILogIt());

        checkNotEquals(test, null, METHOD_NAME);
        checkClass(test.getClass(), SquareController.class, METHOD_NAME);
    }

    @TestMethod
    public void testCreateServerFail() {
        IServer test = Factory.createServer(Constants.NULL_OBJECT_TYPE, 1, new xxMockSquareController(), new xxMockILogIt());

        checkEquals(test, null, "testCreateServerFail");
    }

    @TestMethod
    public void testCreateClientPass() {
        final String METHOD_NAME = "testCreateClientPass";
        IClient test = Factory.createClient(Constants.BASE_CLIENT, Constants.EMPTY_STRING, 1, Constants.EMPTY_STRING);

        checkNotEquals(test, null, METHOD_NAME);
        checkClass(test.getClass(), Client.class, METHOD_NAME);
    }

    @TestMethod
    public void testCreateClientFail() {
        IClient test = Factory.createClient(Constants.NULL_OBJECT_TYPE, Constants.EMPTY_STRING, 1, Constants.EMPTY_STRING);

        checkEquals(test, null, "testCreateClientFail");
    }

    @TestMethod
    public void testCreateUtilityPass() {
        final String METHOD_NAME = "testCreateUtilityPass";
        IUtility test = Factory.createUtility(Constants.BASE_UTILITY);

        checkNotEquals(test, null, METHOD_NAME);
        checkClass(test.getClass(), Utility.class, METHOD_NAME);
    }

    @TestMethod
    public void testCreateUtilityFail() {
        IUtility test = Factory.createUtility(Constants.NULL_OBJECT_TYPE);

        checkEquals(test, null, "testCreateUtilityFail");
    }
}

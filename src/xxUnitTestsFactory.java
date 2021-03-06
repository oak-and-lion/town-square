import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class xxUnitTestsFactory extends xxUnitTestsBaseClass {
    private IFactory factory;

    public xxUnitTestsFactory(String suiteName) {
        super(suiteName);
        this.factory = new Factory();
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
        ISquareKeyPair test = factory.createSquareKeyPair(Constants.BASE_SQUARE_KEY_PAIR, new xxMockIUtility());

        checkNotEquals(test, null, "testCreateSquareKeyPairPass");
        checkClass(test.getClass(), SquareKeyPair.class, "testCreateSquareKeyPairPass");
    }

    @TestMethod
    public void testCreateSquareKeyPairUtilityPass() {
        ISquareKeyPair test = factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, new xxMockIUtility());

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

            ISquareKeyPair test = factory.createSquareKeyPair(Constants.KEYS_SQUARE_KEY_PAIR, kp.getPublic(),
                    kp.getPrivate(), new xxMockIUtility());

            checkNotEquals(test, null, METHOD_NAME);
            checkClass(test.getClass(), SquareKeyPair.class, METHOD_NAME);
        } else {
            forceError(METHOD_NAME);
        }
    }

    @TestMethod
    public void testCreateSquareKeyPairFail() {
        ISquareKeyPair test = factory.createSquareKeyPair(Constants.NULL_OBJECT_TYPE, new xxMockIUtility());

        checkEquals(test, null, "testCreateSquareKeyPairFail");
    }

    @TestMethod
    public void testCreateCryptoUtilsPass() {
        ICryptoUtils test = factory.createCryptoUtils(Constants.BASE_CRYPTO_UTILS, new xxMockIDialogController());

        checkNotEquals(test, null, "testCreateCryptoUtilsPass");
        checkClass(test.getClass(), CryptoUtils.class, "testCreateCryptoUtilsPass");
    }

    @TestMethod
    public void testCreateCryptoUtilsFail() {
        ICryptoUtils test = factory.createCryptoUtils(Constants.NULL_OBJECT_TYPE, null);

        checkEquals(test, null, "testCreateCryptoUtilsFail");
    }

    @TestMethod
    public void testCreateSquareControllerPass() {
        ISquareController test = factory.createSquareController(Constants.BASE_SQUARE_CONTROLLER, new xxMockIUtility(),
                new xxMockIDialogController(), new xxMockILogIt(), new xxMockISquareKeyPair(new xxMockIUtility()));

        checkNotEquals(test, null, "testCreateSquareControllerPass");
        checkClass(test.getClass(), SquareController.class, "testCreateSquareControllerPass");
    }

    @TestMethod
    public void testCreateSquareControllerFail() {
        ISquareController test = factory.createSquareController(Constants.NULL_OBJECT_TYPE, new xxMockIUtility(),
                new xxMockIDialogController(), new xxMockILogIt(), new xxMockISquareKeyPair(new xxMockIUtility()));

        checkEquals(test, null, "testCreateSquareControllerFail");
    }

    @TestMethod
    public void testCreateServerPass() {
        final String METHOD_NAME = "testCreateServerPass";
        IServer test = factory.createServer(Constants.BASE_SQUARE_CONTROLLER, 1, new xxMockSquareController(), new xxMockILogIt(), new xxMockIApp());

        checkNotEquals(test, null, METHOD_NAME);
        checkClass(test.getClass(), SquareController.class, METHOD_NAME);
    }

    @TestMethod
    public void testCreateServerFail() {
        IServer test = factory.createServer(Constants.NULL_OBJECT_TYPE, 1, new xxMockSquareController(), new xxMockILogIt(), new xxMockIApp());

        checkEquals(test, null, "testCreateServerFail");
    }

    @TestMethod
    public void testCreateClientPass() {
        final String METHOD_NAME = "testCreateClientPass";
        IClient test = factory.createClient(Constants.BASE_CLIENT, Constants.EMPTY_STRING, 1, Constants.EMPTY_STRING, null);

        checkNotEquals(test, null, METHOD_NAME);
        checkClass(test.getClass(), Client.class, METHOD_NAME);
    }

    @TestMethod
    public void testCreateClientFail() {
        IClient test = factory.createClient(Constants.NULL_OBJECT_TYPE, Constants.EMPTY_STRING, 1, Constants.EMPTY_STRING, null);

        checkEquals(test, null, "testCreateClientFail");
    }

    @TestMethod
    public void testCreateUtilityPass() {
        final String METHOD_NAME = "testCreateUtilityPass";
        IUtility test = factory.createUtility(Constants.BASE_UTILITY, new xxMockIDialogController());

        checkNotEquals(test, null, METHOD_NAME);
        checkClass(test.getClass(), Utility.class, METHOD_NAME);
    }

    @TestMethod
    public void testCreateUtilityFail() {
        IUtility test = factory.createUtility(Constants.NULL_OBJECT_TYPE, null);

        checkEquals(test, null, "testCreateUtilityFail");
    }
}

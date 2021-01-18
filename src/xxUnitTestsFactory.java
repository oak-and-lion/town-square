public class xxUnitTestsFactory extends xxUnitTestsBaseClass {
    public xxUnitTestsFactory(String suiteName) {
        super(suiteName);
    }

    public void go() {
        testCreateSquareKeyPairPass();
        testCreateSquareKeyPairFail();

        finish();
    }

    public void testCreateSquareKeyPairPass() {
        ISquareKeyPair test = Factory.createSquareKeyPair(Constants.BASE_SQUARE_KEY_PAIR);

        checkNotEquals(test, null, "testCreateSquareKeyPairPass");
        checkClass(test.getClass(), SquareKeyPair.class, "testCreateSquareKeyPairPass");
    }
    public void testCreateSquareKeyPairFail() {
        ISquareKeyPair test = Factory.createSquareKeyPair(Constants.NULL_OBJECT_TYPE);

        checkEquals(test, null, "testCreateSquareKeyPairFail");
    }
    public void testCreateCryptoUtilsPass() {
        ICryptoUtils test = Factory.createCryptoUtils(Constants.BASE_CRYPTO_UTILS);

        checkNotEquals(test, null, "testCreateCryptoUtilsPass");
        checkClass(test.getClass(), CryptoUtils.class, "testCreateCryptoUtilsPass");
    }
    public void testCreateCryptoUtilsFail() {
        ICryptoUtils test = Factory.createCryptoUtils(Constants.NULL_OBJECT_TYPE);

        checkEquals(test, null, "testCreateCryptoUtilsFail");
    }
}

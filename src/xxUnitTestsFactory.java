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
    }

    public void testCreateSquareKeyPairFail() {
        ISquareKeyPair test = Factory.createSquareKeyPair(Constants.NULL_OBJECT_TYPE);

        checkEquals(test, null, "testCreateSquareKeyPairFail");
    }
}

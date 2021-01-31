public class Test {
    public static void main(String[] args) {
        IFactory factory = new Factory();
        IUtility utility = Utility.create();

        String[] s = utility.searchFile("my_square.members", "asdfsafasddsafdsaf", false, -1);
        ILogIt logger = factory.createLogger(1, "test.log", utility);
        logger.logInfo(Integer.toString(s.length));

    }
}

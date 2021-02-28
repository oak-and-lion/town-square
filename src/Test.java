public class Test {
    public static void main(String[] args) {
        IFactory factory = new Factory();
        IUtility utility = Utility.create(new DialogControllerEmpty(new AppEmpty()), factory);

        String[] s = utility.searchFile("my_square.members", "asdfsafasddsafdsaf", false, -1);
        ILogIt logger = factory.createLogger(1, "test.log", utility, null);
        logger.logInfo(Integer.toString(s.length));

    }
}

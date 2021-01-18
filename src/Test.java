public class Test {
    public static void main(String[] args) {
        IUtility utility = Utility.create();

        String[] s = utility.searchFile("my_square.members", "asdfsafasddsafdsaf", false, -1);
        ILogIt logger = Factory.createLogger(1, "test.log", utility);
        logger.logInfo(Integer.toString(s.length));
    }
}

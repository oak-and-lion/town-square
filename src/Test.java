public class Test {
    public static void main(String[] args) {
        Utility utility = Utility.create();

        String[] s = utility.searchFile("my_square.members", "asdfsafasddsafdsaf", false, -1);
        LogIt.LogInfo(Integer.toString(s.length));
    }
}

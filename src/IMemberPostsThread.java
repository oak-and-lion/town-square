public interface IMemberPostsThread {
    void start();
    boolean isWorkDone();
    void run();
    void processPostData(String[] responseSplit, String[] member);
}

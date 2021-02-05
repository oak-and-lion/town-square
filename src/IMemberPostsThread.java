public interface IMemberPostsThread extends IWorkerThread {
    void start();
    boolean isWorkDone();
    void run();
    void processPostData(String[] responseSplit, String[] member);
    PostMessage[] getAllPosts();
}

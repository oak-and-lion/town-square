public interface IClientThread {
    void start();
    void run(int maxRuns);
    void addPostMessage(PostMessage message);
}

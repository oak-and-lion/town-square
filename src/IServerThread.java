public interface IServerThread {
    void start();
    Boolean isDone();
    void closeSocket();
}

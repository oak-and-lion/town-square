public class ClientThread extends Thread {
    private String squareName;
    private ISquareController controller;
    private int lastKnownPost;
    private Square square;
    private Utility utility;

    public ClientThread(Square s, Utility utility) {
        squareName = s.getName();
        controller = s.getController();
        lastKnownPost = s.getLastKnownPost();
        square = s;
        this.utility = utility;
    }

	@Override
    public void run() {
        try {
            
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}

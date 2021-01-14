import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class ClientThread extends Thread {
    private String squareName;
    private ISquareController controller;
    private int lastKnownPost;
    private Square square;
    private Utility utility;
    private boolean process;

    public ClientThread(Square s, Utility utility) {
        squareName = s.getName();
        controller = s.getController();
        lastKnownPost = s.getLastKnownPost();
        square = s;
        this.utility = utility;
        process = true;
        squareName = square.getName();
    }

    public void setLastKnownPost(int index) {
        lastKnownPost = index;
    }

    public String getSquareName() {
        return squareName;
    }

    @Override
    public void run() {
        try {
            while (process) {
                if (square.getSafeLowerName().equals("my_square")) {                    
                    String raw = utility.readFile(square.getSafeLowerName() + ".posts", lastKnownPost);
                    if (!raw.equals("")) {
                        String[] posts = raw.split("\n");
                        ScrollPane scrollPane = square.getPostsScrollPane();
                        VBox vbox = square.getPostsVBox();
                        if (scrollPane == null || vbox == null) {
                            continue;
                        }
                        for(String postInfo : posts) {
                            String[] post = postInfo.split("~_~");
                            String[] members = utility.searchFile(square.getSafeLowerName() + ".members", post[2], false);
                            if (members.length > 0 && members[0] != null) {
                                String[] memberName = members[0].split("~_~");
                                SimpleDateFormat sdf = new SimpleDateFormat();
                                String message = sdf.format(new Date(Long.valueOf(post[0]))) + " (" + memberName[0] + ") : " + post[1];
                                Platform.runLater(new Runnable() {
                                    @Override public void run() {
                                        square.getSampleController().addPostMessages(vbox, scrollPane, message);
                                    }
                                });
                            }                       
                        }
                        setLastKnownPost(lastKnownPost + posts.length);
                        square.setLastKnownPost(lastKnownPost + posts.length);
                    }
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}

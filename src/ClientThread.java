import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class ClientThread extends Thread {
    private String squareName;
    private int lastKnownPost;
    private Square square;
    private Utility utility;
    private boolean process;
    private String uniqueId;

    private static final String EMPTY_STRING = "";
    private static final String DATA_SEPARATOR = "~_~";
    private static final String REQUEST_DATA_SEPARATOR = "%%%";
    private static final String POSTS_FILE_EXT = ".posts";
    private static final String MEMBERS_FILE_EXT = ".members";
    private static final String ENCRYPT_FLAG = "u";
    private static final String READ_COMMAND = "read";

    public ClientThread(Square s, Utility utility, String uniqueId) {
        squareName = s.getName();
        lastKnownPost = s.getLastKnownPost();
        square = s;
        this.utility = utility;
        process = true;
        squareName = square.getName();
        this.uniqueId = uniqueId;
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
            String file = square.getSafeLowerName() + POSTS_FILE_EXT;
            String raw;
            while (process) {
                raw = utility.readFile(file, lastKnownPost);
                if (!raw.equals(EMPTY_STRING)) {
                    processMessages(raw);
                }

                raw = utility.readLastLineOfFile(file);

                /*if (!raw.equals(EMPTY_STRING)) {
                    String[] msg = raw.split(DATA_SEPARATOR);
                    Client client = new Client(square);
                    String response = client.sendMessage(ENCRYPT_FLAG + REQUEST_DATA_SEPARATOR + square.getInvite() + REQUEST_DATA_SEPARATOR
                            + READ_COMMAND + DATA_SEPARATOR + msg[0] + DATA_SEPARATOR + uniqueId, false);
                    LogIt.LogInfo(response);
                }*/
                Thread.sleep(1000);
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private void processMessages(String raw) {
        String[] posts = raw.split("\n");
        ScrollPane scrollPane = square.getPostsScrollPane();
        VBox vbox = square.getPostsVBox();
        if (scrollPane == null || vbox == null) {
            return;
        }
        for (String postInfo : posts) {
            String[] post = postInfo.split(DATA_SEPARATOR);
            String[] members = utility.searchFile(square.getSafeLowerName() + MEMBERS_FILE_EXT, post[2], false);
            if (members.length > 0 && members[0] != null) {
                String[] memberName = members[0].split(DATA_SEPARATOR);
                SimpleDateFormat sdf = new SimpleDateFormat();
                String message = sdf.format(new Date(Long.valueOf(post[0]))) + " (" + memberName[0] + ") : " + post[1];
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        square.getSampleController().addPostMessages(vbox, scrollPane, message);
                    }
                });
            }
        }
        setLastKnownPost(lastKnownPost + posts.length);
        square.setLastKnownPost(lastKnownPost + posts.length);
    }
}

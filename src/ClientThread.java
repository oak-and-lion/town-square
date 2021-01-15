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
    private static final String READ_COMMAND = "read";
    private static final String NEWLINE = "\n";
    private static final String OK_RESULT = "200";
    private static final String COLON = ":";
    private static final String NO_POSTS = "-1";

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

                String[] msg;
                if (raw.equals(EMPTY_STRING)) {
                    msg = new String[1];
                    msg[0] = NO_POSTS;
                } else {
                    msg = raw.split(DATA_SEPARATOR);
                }

                String memberRaw = utility.readFile(square.getSafeLowerName() + MEMBERS_FILE_EXT, -1);
                String[] members = memberRaw.split(REQUEST_DATA_SEPARATOR);
                for (String info : members) {
                    getPostsFromOtherMember(info, file, msg);
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private void getPostsFromOtherMember(String info, String file, String[] msg) {
        if (!info.contains(uniqueId)) {
            String[] member = info.split(DATA_SEPARATOR);
            Client client = new Client(member[2], Integer.valueOf(member[3]), square.getInvite());
            String response = client.sendMessage(
                    READ_COMMAND + REQUEST_DATA_SEPARATOR + msg[0] + REQUEST_DATA_SEPARATOR + uniqueId,
                    false);
            if (!response.equals(EMPTY_STRING)) {
                String[] responseSplit = response.split(COLON);
                if (responseSplit.length == 2 && responseSplit[0].equals(OK_RESULT)
                        && responseSplit[1].equals(EMPTY_STRING)) {
                    String posts = NEWLINE + responseSplit[1].replace(REQUEST_DATA_SEPARATOR, NEWLINE);
                    utility.appendToFile(file, posts);
                }
            }
        }
    }

    private void processMessages(String raw) {
        String[] posts = raw.split(REQUEST_DATA_SEPARATOR);
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

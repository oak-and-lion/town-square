import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class ClientThread extends Thread {
    private String squareName;
    private int lastKnownPost;
    private Square square;
    private IUtility utility;
    private boolean process;
    private String uniqueId;

    public ClientThread(Square s, IUtility utility, String uniqueId) {
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
            String file = square.getSafeLowerName() + Constants.POSTS_FILE_EXT;
            String memberFile = square.getSafeLowerName() + Constants.MEMBERS_FILE_EXT;
            String raw;
            while (process) {
                raw = utility.readFile(file, lastKnownPost);
                if (!raw.equals(Constants.EMPTY_STRING)) {
                    processMessages(raw);
                }

                raw = utility.readLastLineOfFile(file);

                String[] msg;
                if (raw.equals(Constants.EMPTY_STRING)) {
                    msg = new String[1];
                    msg[0] = Constants.NO_POSTS;
                } else {
                    msg = raw.split(Constants.DATA_SEPARATOR);
                }

                String memberRaw = utility.readFile(memberFile,
                        Constants.NOT_FOUND_ROW);
                String[] members = memberRaw.split(Constants.COMMAND_DATA_SEPARATOR);
                for (String info : members) {
                    getMembersFromOtherMembers(info, memberFile);
                }

                memberRaw = utility.readFile(memberFile,
                        Constants.NOT_FOUND_ROW);
                members = memberRaw.split(Constants.COMMAND_DATA_SEPARATOR);
                for (String info : members) {
                    getPostsFromOtherMembers(info, file, msg);
                }

                Thread.sleep(1000);
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private void getMembersFromOtherMembers(String info, String file) {
        if (!info.contains(uniqueId)) {
            String[] member = info.split(Constants.DATA_SEPARATOR);
            IClient client = Factory.createClient(Constants.BASE_CLIENT, member[2], Integer.valueOf(member[3]), square.getInvite());
            String response = client.sendMessage(Constants.MEMBER_COMMAND + Constants.COMMAND_DATA_SEPARATOR + uniqueId, false);
            if (!response.equals(Constants.EMPTY_STRING)) {
                findNewMembers(response, file);
            }
        }
    }

    private void findNewMembers(String response, String file) {
        String[] responseSplit = response.split(Constants.COLON);
                if (responseSplit.length == 2 && responseSplit[0].equals(Constants.OK_RESULT)
                        && !responseSplit[1].equals(Constants.EMPTY_STRING)) {
                    String newLine = Constants.NEWLINE;
                    if (!utility.checkFileExists(file)) {
                        newLine = Constants.EMPTY_STRING;
                    }
                    String[] members = responseSplit[1].split(Constants.COMMAND_DATA_SEPARATOR);
                    for (String memberLoop : members) {
                        String[] memberInfo = memberLoop.split(Constants.DATA_SEPARATOR);
                        String[] memberSearch = utility.searchFile(file, memberInfo[4], Constants.SEARCH_CONTAINS);
                        if (memberSearch.length == 0) {
                            utility.appendToFile(file, newLine + memberLoop);
                            newLine = Constants.NEWLINE;
                        }
                    }
                }
    }

    private void getPostsFromOtherMembers(String info, String file, String[] msg) {
        if (!info.contains(uniqueId)) {
            String[] member = info.split(Constants.DATA_SEPARATOR);
            IClient client = Factory.createClient(Constants.BASE_CLIENT, member[2], Integer.valueOf(member[3]),
                    square.getInvite());
            String response = client.sendMessage(Constants.READ_COMMAND + Constants.COMMAND_DATA_SEPARATOR + msg[0]
                    + Constants.COMMAND_DATA_SEPARATOR + uniqueId, false);
            if (!response.equals(Constants.EMPTY_STRING)) {
                String[] responseSplit = response.split(Constants.COLON);
                if (responseSplit.length == 2 && responseSplit[0].equals(Constants.OK_RESULT)
                        && !responseSplit[1].equals(Constants.EMPTY_STRING)) {
                    String newLine = Constants.NEWLINE;
                    if (!utility.checkFileExists(file)) {
                        newLine = Constants.EMPTY_STRING;
                    }
                    String posts = newLine
                            + responseSplit[1].replace(Constants.COMMAND_DATA_SEPARATOR, Constants.NEWLINE);
                    utility.appendToFile(file, posts);
                    String[] msgs = responseSplit[1].split(Constants.COMMAND_DATA_SEPARATOR);
                    String[] lastMsg = msgs[msgs.length - 1].split(Constants.DATA_SEPARATOR);
                    msg[0] = lastMsg[0];
                }
            }
        }
    }

    private void processMessages(String raw) {
        String[] posts = raw.split(Constants.COMMAND_DATA_SEPARATOR);
        ScrollPane scrollPane = square.getPostsScrollPane();
        VBox vbox = square.getPostsVBox();
        if (scrollPane == null || vbox == null) {
            return;
        }
        for (String postInfo : posts) {
            String[] post = postInfo.split(Constants.DATA_SEPARATOR);
            String[] members = utility.searchFile(square.getSafeLowerName() + Constants.MEMBERS_FILE_EXT, post[2],
                    false);
            if (members.length > 0 && members[0] != null) {
                String[] memberName = members[0].split(Constants.DATA_SEPARATOR);
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
        int temp = lastKnownPost + posts.length;
        setLastKnownPost(temp);
        square.setLastKnownPost(temp);
    }
}

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class ClientThread extends Thread implements IClientThread {
    private String squareName;
    private int lastKnownPost;
    private ISquare square;
    private IUtility utility;
    private boolean process;
    private String uniqueId;
    private PostMessageList posts;
    private IFactory factory;
    private int waitTime;

    public ClientThread(ISquare s, IUtility utility, String uniqueId, IFactory factory) {
        squareName = s.getName();
        lastKnownPost = s.getLastKnownPost();
        square = s;
        this.utility = utility;
        process = true;
        squareName = square.getName();
        this.uniqueId = uniqueId;
        posts = new PostMessageList();
        this.factory = factory;
        getWaitTime();
    }

    private void getWaitTime() {
        if (utility.checkFileExists(Constants.WAIT_TIME_FILE)) {
            waitTime = Integer.valueOf(utility.readFile(Constants.WAIT_TIME_FILE));
        } else {
            waitTime = Constants.DEFAULT_WAIT_TIME;
        }
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
            String memberAliasFile = square.getSafeLowerName() + Constants.ALIAS_FILE_EXT;
            String raw;
            while (process) {
                if (utility.checkFileExists(square.getSafeLowerName() + Constants.PAUSE_FILE_EXT)) {
                    Thread.sleep(Constants.PAUSE_WAIT_TIME);
                    continue;
                }
                raw = utility.readFile(file, lastKnownPost);
                if (!raw.equals(Constants.EMPTY_STRING)) {
                    processMessages(raw);
                }

                String[] msg = new String[1];
                msg[0] = Constants.NO_POSTS;

                String[] members = getAllMembers(memberFile, memberAliasFile);

                for (String info : members) {
                    getMembersFromOtherMembers(info, memberFile);
                }

                ArrayList<IMemberPostsThread> memberThreads = new ArrayList<>();
                boolean threadsDone = false;
                for (String info : members) {
                    // spin up new thread for each member and process ASAP
                    IMemberPostsThread thread = factory.createMemberPostsThread(Constants.BASE_MEMBER_POSTS_THREAD, info, uniqueId, msg, square, utility);
                    memberThreads.add(thread);
                    thread.start();
                }

                checkForDoneThreads(threadsDone, memberThreads);

                for (IMemberPostsThread mt : memberThreads) {
                    posts.addAll(mt.getAllPosts());
                }

                updatePosts(file);

                Thread.sleep(waitTime);
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private void checkForDoneThreads(boolean threadsDone, ArrayList<IMemberPostsThread> memberThreads) {
        while (!threadsDone) {
            threadsDone = true;
            for (int x = 0; x < memberThreads.size(); x++) {
                if (!memberThreads.get(x).isWorkDone()) {
                    threadsDone = false;
                    break;
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    private String[] getAllMembers(String memberFile, String memberAliasFile) {
        String memberRaw = utility.readFile(memberFile, Constants.NOT_FOUND_ROW);

        String[] memberAliases = utility.readFile(memberAliasFile, Constants.NOT_FOUND_ROW)
                .split(Constants.READ_FILE_DATA_SEPARATOR);

        String[] members = memberRaw.split(Constants.COMMAND_DATA_SEPARATOR);
        ArrayList<String> memberWork = new ArrayList<>();
        memberWork.addAll(Arrays.asList(members));
        for (String info : members) {
            String[] member = info.split(Constants.DATA_SEPARATOR);
            for (String alias : memberAliases) {
                if (alias.equals(Constants.EMPTY_STRING)) {
                    continue;
                }
                String[] aliasSplit = alias.split(Constants.QUESTION_MARK_SPLIT);
                String[] aliasInfo = aliasSplit[0].split(Constants.COLON);
                if (info.contains(aliasInfo[1])) {
                    String[] aliases = aliasSplit[1].split(Constants.FORWARD_SLASH);
                    for (String a : aliases) {
                        String[] a1 = a.split(Constants.COLON);
                        memberWork.add(member[0] + Constants.DATA_SEPARATOR + member[1] + Constants.DATA_SEPARATOR
                                + a1[0] + Constants.DATA_SEPARATOR + a1[1] + Constants.DATA_SEPARATOR + aliasInfo[1]);
                    }
                }
            }
        }
        return memberWork.toArray(new String[memberWork.size()]);
    }

    private void getMembersFromOtherMembers(String info, String file) {
        if (!info.contains(uniqueId) && !info.startsWith(Constants.STAR)) {
            String[] member = info.split(Constants.DATA_SEPARATOR);
            IClient client = Factory.createClient(Constants.BASE_CLIENT, member[2], Integer.valueOf(member[3]),
                    square.getInvite());
            String response = client.sendMessage(Constants.MEMBER_COMMAND + Constants.COMMAND_DATA_SEPARATOR + uniqueId,
                    false);
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
                } else if (memberInfo[0].equals(Constants.EXIT_SQUARE_TEXT)) {
                    removeMember(file, memberInfo[4]);
                }
            }
        }
    }

    private void removeMember(String file, String memberId) {
        String[] currentMembers = utility.readFile(file).split(Constants.READ_FILE_DATA_SEPARATOR);
        StringBuilder result = new StringBuilder();
        int count = 0;
        for (String currentMember : currentMembers) {
            String[] cm = currentMember.split(Constants.FILE_DATA_SEPARATOR);
            if (count > 0) {
                result.append(Constants.NEWLINE);
            }
            if (!cm[4].equals(memberId)) {                
                result.append(currentMember);                
            } else {
                result.append(Constants.STAR + cm[0] + Constants.FILE_DATA_SEPARATOR + Constants.NULL_TEXT +
                                Constants.FILE_DATA_SEPARATOR + Constants.NULL_TEXT + Constants.FILE_DATA_SEPARATOR +
                                Constants.NULL_TEXT + Constants.FILE_DATA_SEPARATOR + cm[4]);
            }
            count++;
        }
        utility.writeFile(file, result.toString());
    }

    private void processMessages(String raw) {
        String[] clientPosts = raw.split(Constants.COMMAND_DATA_SEPARATOR);
        ScrollPane scrollPane = square.getPostsScrollPane();
        VBox vbox = square.getPostsVBox();
        if (scrollPane == null || vbox == null) {
            return;
        }
        for (String postInfo : clientPosts) {
            if (postInfo.equals(Constants.EMPTY_STRING)) {
                continue;
            }
            String[] post = postInfo.split(Constants.DATA_SEPARATOR);
            posts.add(new PostMessage(Long.parseLong(post[0]), postInfo));
            String[] members = utility.searchFile(square.getSafeLowerName() + Constants.MEMBERS_FILE_EXT, post[2],
                    false);
            if (members.length > 0 && members[0] != null) {
                String[] memberName = members[0].split(Constants.DATA_SEPARATOR);
                SimpleDateFormat sdf = new SimpleDateFormat();
                String message = sdf.format(new Date(Long.valueOf(post[0]))) + " (" + memberName[0] + ") : " + post[1];
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        square.getSampleController().addPostMessages(square, vbox, scrollPane, message,
                                Long.parseLong(post[0]), memberName[4]);
                    }
                });
            }
        }
    }

    private void updatePosts(String file) {
        writePostFile(file);
    }

    private void writePostFile(String file) {
        if (posts.size() == 0) {
            return;
        }
        utility.deleteFile(file);
        utility.writeFile(file, String.join(Constants.NEWLINE, posts.getAllMessages()));
    }

    public void addPostMessage(PostMessage message) {
        posts.add(message);
    }
}

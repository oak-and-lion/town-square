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
    private int maxRuns;
    private IApp app;
    private ISquareKeyPair tempKeys;

    public ClientThread(ISquare s, IUtility utility, String uniqueId, IFactory factory, IApp app) {
        square = s;
        this.utility = utility;
        process = true;
        squareName = square.getName();
        lastKnownPost = square.getLastKnownPost();
        this.uniqueId = uniqueId;
        posts = new PostMessageList();
        this.factory = factory;
        this.maxRuns = Constants.INFINITE_LOOP_FLAG;
        getWaitTime();
        this.app = app;
        tempKeys = factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility);
        tempKeys.setPrivateKeyFromBase64(utility.readFile(Constants.PRIVATE_KEY_FILE));
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

    public void run(int maxRuns) {
        this.maxRuns = maxRuns;
        run();
    }

    @Override
    public void run() {
        try {
            String file = utility.concatStrings(square.getSafeLowerName(), Constants.POSTS_FILE_EXT);
            String memberFile = utility.concatStrings(square.getSafeLowerName(), Constants.MEMBERS_FILE_EXT);

            String raw;
            int count = 0;

            while (process) {
                if (utility
                        .checkFileExists(utility.concatStrings(square.getSafeLowerName(), Constants.PAUSE_FILE_EXT))) {
                    Thread.sleep(Constants.PAUSE_WAIT_TIME);
                    continue;
                }

                String[] msg = new String[1];
                msg[0] = Constants.NO_POSTS;

                String[] members = getAllMembers(memberFile);

                for (String info : members) {
                    getMembersFromOtherMembers(info, memberFile);
                }

                members = getAllMembers(memberFile);

                ArrayList<IMemberPostsThread> memberThreads = new ArrayList<>();

                for (String info : members) {
                    // spin up new thread for each member and process ASAP
                    IMemberPostsThread thread = factory.createMemberPostsThread(Constants.BASE_MEMBER_POSTS_THREAD,
                            info, uniqueId, msg, square, utility);
                    memberThreads.add(thread);
                    thread.start();
                }

                performWork(memberThreads);

                for (IMemberPostsThread mt : memberThreads) {
                    posts.addAll(mt.getAllPosts());
                }

                updatePosts(file);

                raw = utility.readFile(file, lastKnownPost);
                if (!raw.equals(Constants.EMPTY_STRING)) {
                    processMessages(raw);
                }

                Thread.sleep(waitTime);

                count = checkCount(count);
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private int checkCount(int count) {
        count++;
        if (maxRuns == Constants.INFINITE_LOOP_FLAG) {
            count = Constants.REALLY_LOW_NUMBER;
        } else if (count >= maxRuns) {
            process = false;
        }

        return count;
    }

    private void performWork(ArrayList<IMemberPostsThread> memberThreads) {
        boolean threadsDone = false;
        ArrayList<IWorkerThread> workers = new ArrayList<>();
        for (IMemberPostsThread memberThread : memberThreads) {
            workers.add(memberThread);
        }
        checkForDoneThreads(threadsDone, workers);
    }

    private void checkForDoneThreads(boolean threadsDone, ArrayList<IWorkerThread> memberThreads) {
        while (!threadsDone) {
            threadsDone = true;
            for (int x = 0; x < memberThreads.size(); x++) {
                if (!memberThreads.get(x).isWorkDone()) {
                    threadsDone = false;
                    break;
                }
            }
            try {
                Thread.sleep(Constants.TINY_PAUSE);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    private String[] getAllMembers(String memberFile) {
        ArrayList<String> memberWork = new ArrayList<>();

        if (utility.checkFileExists(Constants.HUB_REGISTRATION_FILE)) {
            String hubInfo = utility.readFile(Constants.HUB_REGISTRATION_FILE);
            String[] members = utility.searchFile(memberFile, utility.readFile(Constants.UNIQUE_ID_FILE),
                    Constants.SEARCH_CONTAINS);
            if (members.length > 0) {
                String[] memberInfo = members[0].split(Constants.FILE_DATA_SEPARATOR);
                String m = utility.concatStrings(memberInfo[0], Constants.FILE_DATA_SEPARATOR, memberInfo[1],
                        Constants.FILE_DATA_SEPARATOR, hubInfo, Constants.FILE_DATA_SEPARATOR, memberInfo[4]);
                memberWork.add(m);
            }
        } else {
            String memberRaw = utility.readFile(memberFile, Constants.NOT_FOUND_ROW);

            String[] members = memberRaw.split(Constants.COMMAND_DATA_SEPARATOR);

            memberWork.addAll(Arrays.asList(members));
        }
        return memberWork.toArray(new String[memberWork.size()]);
    }

    private void getMembersFromOtherMembers(String info, String file) {
        if (!(info.contains(uniqueId) && (info.contains(square.getIP()) && info.contains(square.getPort())))
                && !info.startsWith(Constants.STAR)) {
            String[] member = info.split(Constants.DATA_SEPARATOR);
            tempKeys.setPublicKeyFromBase64(member[1]);
            IClient client = factory.createClient(Constants.BASE_CLIENT, member[2], Integer.valueOf(member[3]),
                    square.getInvite(), app);
            String password = utility.generateRandomString(Constants.ENCRYPTION_KEY_LENGTH);
            String temp = utility.concatStrings(Constants.MEMBER_COMMAND, Constants.COMMAND_DATA_SEPARATOR, uniqueId);
            String encrypted = utility.concatStrings(tempKeys.encryptToBase64(password),
                    Constants.COMMAND_DATA_SEPARATOR, utility.encrypt(temp, password));
            String response = client.sendMessage(encrypted, Constants.ENCRYPT_CLIENT_TRANSFER,
                    Constants.MEMBER_COMMAND);
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
                    utility.appendToFile(file, utility.concatStrings(newLine, memberLoop));
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
                result.append(utility.concatStrings(Constants.STAR, cm[0], Constants.FILE_DATA_SEPARATOR,
                        Constants.NULL_TEXT, Constants.FILE_DATA_SEPARATOR, Constants.NULL_TEXT,
                        Constants.FILE_DATA_SEPARATOR, Constants.NULL_TEXT, Constants.FILE_DATA_SEPARATOR, cm[4]));
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
            String[] members = utility.searchFile(
                    utility.concatStrings(square.getSafeLowerName(), Constants.MEMBERS_FILE_EXT), post[2], false);
            if (members.length > 0 && members[0] != null) {
                String[] memberName = members[0].split(Constants.DATA_SEPARATOR);
                SimpleDateFormat sdf = new SimpleDateFormat();
                String message = utility.concatStrings(sdf.format(new Date(Long.valueOf(post[0]))), Constants.SPACE,
                        Constants.OPEN_PARENS, memberName[0], Constants.CLOSE_PARENS, Constants.SPACE, Constants.COLON,
                        Constants.SPACE, post[1]);
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
        if (utility.checkFileExists(Constants.HUB_REGISTRATION_FILE)) {
            postToHub(message);
        } else {
            posts.add(message);
        }
    }

    private void postToHub(PostMessage message) {        
        String hubInfo = utility.readFile(Constants.HUB_REGISTRATION_FILE);
        String[] members = utility.searchFile(
                utility.concatStrings(square.getSafeLowerName(), Constants.MEMBERS_FILE_EXT),
                utility.readFile(Constants.UNIQUE_ID_FILE), Constants.SEARCH_CONTAINS);
        if (members.length > 0) {
            String[] hub = hubInfo.split(Constants.FILE_DATA_SEPARATOR);
            String[] memberInfo = members[0].split(Constants.FILE_DATA_SEPARATOR);
            tempKeys.setPublicKeyFromBase64(memberInfo[1]);
            String password = utility.generateRandomString(Constants.ENCRYPTION_KEY_LENGTH);
            String encrypted = utility.encrypt(message.getMessage(), password);
            String encryptedPassword = tempKeys.encryptToBase64(password);
            String text = utility.concatStrings(encryptedPassword, Constants.COMMAND_DATA_SEPARATOR, encrypted);
            IClient client = factory.createClient(Constants.BASE_CLIENT, hub[0], Integer.parseInt(hub[1]), square.getId(), app);
            String result = client.sendMessage(text, Constants.ENCRYPT_CLIENT_TRANSFER, Constants.SEND_MESSAGE);
            SquareResponse response = new SquareResponse(result);
            if (response.getCode().equals(Constants.OK_RESULT)) {
                // need code
            } else {
                // backup locally, in case it doesn't work
                posts.add(message);
            }
        }
    }
}

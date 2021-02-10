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
    private int aliasFileHash;

    public ClientThread(ISquare s, IUtility utility, String uniqueId, IFactory factory) {
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
            String memberAliasFile = utility.concatStrings(square.getSafeLowerName(), Constants.ALIAS_FILE_EXT);
            aliasFileHash = Arrays.hashCode(utility.readBinaryFile(memberAliasFile));
            int newHashCode = 0;
            String raw;
            int count = 0;
            while (process) {
                if (utility
                        .checkFileExists(utility.concatStrings(square.getSafeLowerName(), Constants.PAUSE_FILE_EXT))) {
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
                    getAliasesFromOtherMembers(info, memberAliasFile);
                }

                members = getAllMembers(memberFile, memberAliasFile);

                ArrayList<IMemberPostsThread> memberThreads = new ArrayList<>();
                ArrayList<IMemberAliasUpdateThread> memberAliasThreads = new ArrayList<>();

                for (String info : members) {
                    // spin up new thread for each member and process ASAP
                    IMemberPostsThread thread = factory.createMemberPostsThread(Constants.BASE_MEMBER_POSTS_THREAD,
                            info, uniqueId, msg, square, utility);
                    memberThreads.add(thread);
                    thread.start();

                    if (newHashCode != aliasFileHash) {
                        IMemberAliasUpdateThread thread2 = factory.createMemberAliasUpdateThread(
                                Constants.BASE_MEMBER_ALIAS_UPDATE_THREAD, info, uniqueId, square, utility);
                        memberAliasThreads.add(thread2);
                        thread2.start();
                    }
                }

                newHashCode = checkHashCodes(newHashCode, memberAliasFile);

                performWork(memberThreads, memberAliasThreads);

                for (IMemberPostsThread mt : memberThreads) {
                    posts.addAll(mt.getAllPosts());
                }

                updatePosts(file);

                Thread.sleep(waitTime);

                count = checkCount(count);
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private int checkHashCodes(int newHashCode, String memberAliasFile) {
        Arrays.hashCode(utility.readBinaryFile(memberAliasFile));

        if (newHashCode != aliasFileHash) {
            aliasFileHash = newHashCode;
        }

        return newHashCode;
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

    private void performWork(ArrayList<IMemberPostsThread> memberThreads,
            ArrayList<IMemberAliasUpdateThread> memberAliasThreads) {
        boolean threadsDone = false;
        ArrayList<IWorkerThread> workers = new ArrayList<>();
        for (IMemberPostsThread memberThread : memberThreads) {
            workers.add(memberThread);
        }
        for (IMemberAliasUpdateThread memberThread : memberAliasThreads) {
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
                if (info.endsWith(aliasSplit[0])) {
                    String[] aliases = aliasSplit[1].split(Constants.FORWARD_SLASH);
                    for (String a : aliases) {
                        String[] a1 = a.split(Constants.COLON);
                        memberWork.add(utility.concatStrings(member[0], Constants.DATA_SEPARATOR, member[1],
                                Constants.DATA_SEPARATOR, a1[0], Constants.DATA_SEPARATOR, a1[1],
                                Constants.DATA_SEPARATOR, aliasSplit[0]));
                    }
                }
            }
        }
        return memberWork.toArray(new String[memberWork.size()]);
    }

    private void getMembersFromOtherMembers(String info, String file) {
        if (!info.contains(uniqueId) && !info.startsWith(Constants.STAR)) {
            String[] member = info.split(Constants.DATA_SEPARATOR);
            IClient client = factory.createClient(Constants.BASE_CLIENT, member[2], Integer.valueOf(member[3]),
                    square.getInvite());
            String response = client.sendMessage(
                    utility.concatStrings(Constants.MEMBER_COMMAND, Constants.COMMAND_DATA_SEPARATOR, uniqueId), Constants.DO_NOT_ENCRYPT_CLIENT_TRANSFER);
            if (!response.equals(Constants.EMPTY_STRING)) {
                findNewMembers(response, file);
            }
        }
    }

    private void getAliasesFromOtherMembers(String info, String file) {
        if (!info.contains(uniqueId) && !info.startsWith(Constants.STAR)) {
            String[] member = info.split(Constants.DATA_SEPARATOR);
            IClient client = factory.createClient(Constants.BASE_CLIENT, member[2], Integer.valueOf(member[3]),
                    square.getInvite());
            String response = client.sendMessage(
                    utility.concatStrings(Constants.READ_ALIAS_COMMAND, utility.concatStrings(Constants.COMMAND_DATA_SEPARATOR, uniqueId)),
                    Constants.DO_NOT_ENCRYPT_CLIENT_TRANSFER);
            if (!response.equals(Constants.EMPTY_STRING)) {
                String[] responseInfo = response.split(Constants.COLON);
                if (responseInfo[0].equals(Constants.OK_RESULT)) {
                    processAliasFileReturn(responseInfo[1]);
                }
            }
        }
    }

    private void processAliasFileReturn(String response) {
        RequesterInfo requester = new RequesterInfo(utility.readFile(Constants.IP_FILE));
        String[] responseInfo = response.split(Constants.COMMAND_DATA_SEPARATOR);
        ISquareKeyPair tempKeys = factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility);
        tempKeys.setPrivateKeyFromBase64(utility.readFile(Constants.PRIVATE_KEY_FILE));
        String password = tempKeys.decryptFromBase64(responseInfo[0]);
        String data = utility.decrypt(responseInfo[1], password);
        if (!data.equals(Constants.EMPTY_STRING)) {
            String[] aliases = data.split(Constants.COMMAND_DATA_SEPARATOR);
            ISquareController squareController = square.getController();
            for (String alias : aliases) {
                String[] temp = alias.split(Constants.QUESTION_MARK_SPLIT);
                String[] addresses = temp[1].split(Constants.FORWARD_SLASH);
                for (String address : addresses) {
                    String[] temp2 = address.split(Constants.COLON);
                    String request = utility.concatStrings(Constants.UNENCRYPTED_FLAG, Constants.COMMAND_DATA_SEPARATOR,
                            square.getInvite(), Constants.COMMAND_DATA_SEPARATOR, Constants.REGISTER_ALIAS_COMMAND,
                            Constants.COMMAND_DATA_SEPARATOR, Constants.NULL_TEXT, Constants.FILE_DATA_SEPARATOR,
                            temp2[0], Constants.FILE_DATA_SEPARATOR, temp2[1], Constants.FILE_DATA_SEPARATOR, temp[0]);
                    squareController.processRequest(request, requester);
                }
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
        posts.add(message);
    }
}

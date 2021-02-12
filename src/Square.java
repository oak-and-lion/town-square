import java.util.concurrent.ThreadLocalRandom;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

public class Square implements ISquare {
    private String name;
    private String invite;
    private String id;
    private String ip;
    private boolean squarePrivate;
    private String port;
    private Object temp;
    private String password;
    private ISquareController controller;
    private IUtility utility;
    private int lastKnownPost;
    private ScrollPane postsScrollPane;
    private VBox postsVBox;
    private IDialogController sampleController;
    private String uniqueId;
    private IClientThread clientThread;
    private Tab tab;
    private IFactory factory;

    public Square(String info, String port, String ip, ISquareController squareController, IUtility utility,
            IDialogController sampleController, String uniqueId, IFactory factory) {
        setPassword(Constants.EMPTY_STRING);
        this.port = port;
        this.ip = ip;
        String[] split = info.split(Constants.COMMA);
        for (int x = 0; x < split.length; x++) {
            if (x == 0) {
                name = split[x];
            } else if (x == 1) {
                invite = split[x];
            } else if (x == 2) {
                id = split[x];
            } else if (x == 3) {
                if (split[x].equals("1")) {
                    squarePrivate = true;
                } else {
                    squarePrivate = false;
                }
            } else if (x == 4) {
                password = split[x];
            }
        }

        this.uniqueId = uniqueId;
        lastKnownPost = -1;
        this.utility = utility;
        controller = squareController;
        this.sampleController = sampleController;
        this.factory = factory;
        initializeClientThread();
    }

    private void initializeClientThread() {
        clientThread = factory.createClientThread(Constants.BASE_CLIENT_THREAD, this, utility, uniqueId);

        if (utility.checkFileExists(utility.concatStrings(getSafeLowerName(), Constants.POSTS_FILE_EXT))) {
            lastKnownPost = utility
                    .countLinesInFile(utility.concatStrings(getSafeLowerName(), Constants.POSTS_FILE_EXT));
        }

        try {
            long millis = ThreadLocalRandom.current().nextLong(1000);
            if (millis < 0) {
                millis *= -1;
            }
            Thread.sleep(millis);
            clientThread.start();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public void runClientFunctions(int maxRuns) {
        clientThread.run(maxRuns);
    }

    public String toString() {
        String isPrivate = "0";
        if (this.isPrivate()) {
            isPrivate = "1";
        }
        return utility.concatStrings(this.name, Constants.COMMA, this.invite, Constants.COMMA, this.id, Constants.COMMA,
                isPrivate, Constants.COMMA, password);
    }

    public String getName() {
        if (utility.checkFileExists(utility.concatStrings(getSafeLowerName(), Constants.NICKNAME_FILE_EXT))) {
            return utility.readFile(utility.concatStrings(getSafeLowerName(), Constants.NICKNAME_FILE_EXT));
        }
        return getTrueName();
    }

    public String getTrueName() {
        return name;
    }

    public String getSafeName() {
        return name.replace(Constants.SPACE, Constants.UNDERSCORE);
    }

    public String getSafeLowerName() {
        return getSafeName().toLowerCase();
    }

    public String getInvite() {
        return invite;
    }

    public String getId() {
        return id;
    }

    public String getIP() {
        return ip;
    }

    public void setIP(String value) {
        ip = value;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String value) {
        port = value;
    }

    public boolean isPrivate() {
        return squarePrivate;
    }

    public void setPrivate(Boolean value) {
        squarePrivate = value;
    }

    public void setTemp(Object o) {
        temp = o;
    }

    public Object getTemp() {
        return temp;
    }

    public String getPassword() {
        if (password.equals(Constants.NO_PASSWORD_VALUE)) {
            return Constants.EMPTY_STRING;
        }
        return password;
    }

    public void setPassword(String value) {
        if (value.equals(Constants.EMPTY_STRING)) {
            value = Constants.NO_PASSWORD_VALUE;
        }
        password = value;
    }

    public ISquareController getController() {
        return controller;
    }

    public int getLastKnownPost() {
        return lastKnownPost;
    }

    public void setLastKnownPost(int value) {
        lastKnownPost = value;
    }

    public void setPostsScrollPane(ScrollPane value) {
        postsScrollPane = value;
    }

    public ScrollPane getPostsScrollPane() {
        return postsScrollPane;
    }

    public void setPostsVBox(VBox value) {
        postsVBox = value;
    }

    public VBox getPostsVBox() {
        return postsVBox;
    }

    public IDialogController getSampleController() {
        return sampleController;
    }

    public void addPostMessage(PostMessage message) {
        clientThread.addPostMessage(message);
    }

    public void setTab(Tab tab) {
        this.tab = tab;
    }

    public Tab getTab() {
        return tab;
    }

    public IFactory getFactory() {
        return this.factory;
    }
}

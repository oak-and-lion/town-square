public class Square {
    private String name;
    private String invite;
    private String id;
    private String ip;
    private boolean squarePrivate;
    private String port;
    private Object temp;
    private String password;
    private ClientThread clientThread;
    private ISquareController controller;
    private Utility utility;
    private int lastKnownPost;

    private static final String COMMA = ",";
    private static final String NO_PASSWORD_VALUE = "~~~~~~~";
    private static final String EMPTY_STRING = "";
    private static final String POSTS_FILE_EXT = ".posts";

    public Square(String info, String port, String ip, ISquareController squareController, Utility utility) {
        setPassword("");
        this.port = port;
        this.ip = ip;
        String[] split = info.split(",");
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

        lastKnownPost = 0;
        this.utility = utility;
        controller = squareController;
        initializeClientThread();
    }

    private void initializeClientThread() {
        clientThread = new ClientThread(this, utility);

        if (utility.checkFileExists(getSafeLowerName() + POSTS_FILE_EXT)) {
            lastKnownPost = utility.countLinesInFile(getSafeLowerName() + POSTS_FILE_EXT);
        }

        clientThread.start();
    }
    
    public String toString() {
        String isPrivate = "0";
        if (this.isPrivate()) {
            isPrivate = "1";
        }
        return this.name + COMMA + this.invite + COMMA + this.id + COMMA + isPrivate + COMMA + password;
    }

    public String getName() {
        return name;
    }

    public String getSafeName() {
        return name.replace(' ', '_');
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

    public String getPort() {
        return port;
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
        if (password.equals(NO_PASSWORD_VALUE)) {
            return EMPTY_STRING;
        }
        return password;
    }

    public void setPassword(String value) {
        if (value.equals("")) {
            value = NO_PASSWORD_VALUE;
        }
        password = value;
    }

    public ISquareController getController() {
        return controller;
    }

    public int getLastKnownPost() {
        return lastKnownPost;
    }
}

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ServerDialogController implements IDialogController {
    private IApp app;
    private String publicKey;
    private IUtility utility;
    private IFactory factory;
    ICommandController commandController;
    private String uniqueId;
    private List<ISquare> squares;
    private List<String> squareInvites;
    private String port;
    private String remoteIP;
    private String defaultName;

    public ServerDialogController(IApp app, IUtility utility, IFactory factory) {
        this.app = app;
        this.utility = utility;
        this.factory = factory;
        publicKey = Constants.EMPTY_STRING;
        uniqueId = utility.readFile(Constants.UNIQUE_ID_FILE);
        port = utility.readFile(Constants.PORT_FILE);
        remoteIP = utility.readFile(Constants.IP_FILE);
        defaultName = utility.readFile(Constants.DEFAULT_NAME_FILE);
        squares = new ArrayList<>();
        squareInvites = new ArrayList<>();
    }

    public void setDefaultName(String name) {
        app.sendDefaultName(name);
        defaultName = name;
    }

    public boolean isGui() {
        return false;
    }

    public IApp getParent() {
        return app;
    }

    public void initErrorLogger() {
        // not needed
    }

    public void updatePauseNotification(ISquare square, boolean paused) {
        // not needed
    }

    public void setTabSquare(ISquare square) {
        // not needed
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setParent(IApp parent) {
        app = parent;
    }

    public void showLicense() {
        // not needed
    }

    public void setVersion(String version) {
        // not needed
    }

    public void setTabName(ISquare square, String s, String s2) {
        // not needed
    }

    public void setPort(String port) {
        app.sendPort(port);
        this.port = port;
    }

    public void showAbout() {
        // not needed
    }

    public String getDefaultName() {
        return app.getDefaultName();
    }

    public void setUniqueId(String uniqueId) {
        utility.writeFile(Constants.UNIQUE_ID_FILE, uniqueId);
    }

    public void processImageAction(int i, String s, long l, ISquare square) {
        // not needed
    }

    public void showList(String[] s, String s2, String s3) {
        // not needed
    }

    public void setFactory(IFactory factory) {
        this.factory = factory;
    }

    public IFactory getFactory() {
        return this.factory;
    }

    public ICommandController getCommandController() {
        return commandController;
    }

    public void resizeControls(double width, double height) {
        // not needed
    }

    public void buildSquares() {
        // not needed
    }

    public void setStage(Stage stage) {
        // not needed
    }

    public void showCloneMessage() {
        // not needed
    }

    public void setUtilityController(IUtility utility) {
        this.utility = utility;
    }

    public void setCommandController(ICommandController commandController) {
        this.commandController = commandController;
    }

    public void updateDefaultNameInMemberFiles(String name) {
        String[] files = utility.getFiles(Constants.MEMBERS_FILE_EXT);

        for (String file : files) {
            String memberInfo = utility.readFile(file);
            String[] lines = memberInfo.split(Constants.READ_FILE_DATA_SEPARATOR);
            int i = 0;
            for (String line : lines) {
                String[] lineData = line.split(Constants.FILE_DATA_SEPARATOR, 2);
                String ipData = lineData[1];
                if (ipData.contains(uniqueId)) {
                    lines[i] = utility.concatStrings(name, Constants.FILE_DATA_SEPARATOR, ipData);
                    String newMemberInfo = String.join(Constants.NEWLINE, lines);
                    utility.deleteFile(file);
                    utility.writeFile(file, newMemberInfo);
                    break;
                }
                i++;
            }
        }
    }

    public void addPostMessages(ISquare square, VBox vbox, ScrollPane scrollPane, String message, long millis,
            String memberId) {
        // not needed
    }

    public void setRemoteIP(ObservableList<IPAddress> ips, String defaultIp) {
        remoteIP = defaultIp;
    }

    public ISquare getSquareByInvite(String id) {
        if (squareInvites.contains(id)) {
            int index = squareInvites.indexOf(id);
            return squares.get(index);
        }

        return null;
    }

    public void processPendingInvites() {
        String[] files = utility.getFiles(Constants.INVITE_FILE_EXT);

        for (String file : files) {
            String invite = utility.readFile(file);
            if (processInvitation(invite)) {
                utility.deleteFile(file);
            }
        }
    }

    public boolean processInvitation(String invite) {
        // sample invitation
        // 0 == encryption flag
        // 1 == ip of the remote host to process invitation
        // 2 == port of remote host
        // 3 == id of the square being invited to
        // u~207.244.84.59~44123~a7075b5b-b91d-4448-a0f9-d9b0bec1a726
        String[] split = invite.split(Constants.TILDE);
        IClient client = factory.createClient(Constants.BASE_CLIENT, split[1], Integer.valueOf(split[2]), split[3],
                getParent());
        boolean encrypt = false;
        ISquareKeyPair tempKeys = factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility);

        if (split[0].equals(Constants.ENCRYPTED_FLAG)) {
            encrypt = true;
        }

        String remotePublicKey = client.sendMessage(Constants.REQUEST_PUBLIC_KEY_COMMAND,
                Constants.DO_NOT_ENCRYPT_CLIENT_TRANSFER, Constants.REQUEST_PUBLIC_KEY_COMMAND);
        if (remotePublicKey.equals(Constants.EMPTY_STRING)) {
            utility.writeFile(utility.concatStrings(Constants.INVITE_FILE_PREFIX, split[3], Constants.INVITE_FILE_EXT),
                    invite);
            return false;
        }
        SquareResponse response = processTCPReturn(remotePublicKey);
        if (!response.getCode().equals(Constants.OK_RESULT)) {
            return false;
        }

        tempKeys.setPublicKeyFromBase64(response.getMessage());

        String data = utility.concatStrings(Constants.JOIN_COMMAND, Constants.COMMAND_DATA_SEPARATOR,
                defaultName, Constants.COMMAND_DATA_SEPARATOR, publicKey, Constants.COMMAND_DATA_SEPARATOR,
                remoteIP, Constants.COMMAND_DATA_SEPARATOR, port,
                Constants.COMMAND_DATA_SEPARATOR, uniqueId);

        if (encrypt) {
            String password = utility.generateRandomString(Constants.ENCRYPTION_KEY_LENGTH);
            data = utility.concatStrings(tempKeys.encryptToBase64(password), Constants.COMMAND_DATA_SEPARATOR,
                    utility.encrypt(data, password));
        }

        response = processTCPReturn(client.sendMessage(data, encrypt, Constants.JOIN_COMMAND));

        if (response.getCode().equals(Constants.OK_RESULT)
                || response.getCode().equals(Constants.ALREADY_REGISTERED_RESULT)) {
            String[] responseData = response.getResponseSplit();
            String squareSafeName = safeString(responseData[3]);
            utility.deleteFile(utility.concatStrings(squareSafeName, Constants.PAUSE_FILE_EXT));
            utility.deleteFile(utility.concatStrings(squareSafeName, Constants.LEAVE_FILE_EXT));
            String temp = utility.concatStrings(Constants.MEMBER_COMMAND, Constants.COMMAND_DATA_SEPARATOR,
                    uniqueId);
            String password = utility.generateRandomString(Constants.ENCRYPTION_KEY_LENGTH);
            data = utility.concatStrings(tempKeys.encryptToBase64(password), Constants.COMMAND_DATA_SEPARATOR,
                    utility.encrypt(temp, password));
            response = processTCPReturn(client.sendMessage(data, encrypt, Constants.MEMBER_COMMAND));
            utility.writeFile(utility.concatStrings(squareSafeName, Constants.MEMBERS_FILE_EXT),
                    response.getMessage().replace(Constants.COMMAND_DATA_SEPARATOR, Constants.NEWLINE));
            String info = utility.concatStrings(responseData[3], Constants.COMMA, client.getSquareId(), Constants.COMMA,
                    Constants.TAB_PREFIX, squareSafeName, Constants.COMMA, Constants.ZERO, Constants.NO_PASSWORD_VALUE);
            ISquare square = factory.createSquare(Constants.BASE_SQUARE, info, port,
                    remoteIP,
                    factory.createSquareController(Constants.BASE_SQUARE_CONTROLLER, utility, this,
                            factory.createLogger(getParent().getLoggerType(),
                                    utility.concatStrings(uniqueId, Constants.LOG_FILE_EXT), utility, app.getDialogController()),
                            factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility)),
                    utility, this, uniqueId, getParent());
            utility.writeFile(utility.concatStrings(squareSafeName, Constants.SQUARE_FILE_EXT), info);
            setTabSquare(square);

            return true;
        }

        return false;
    }

    private SquareResponse processTCPReturn(String result) {
        return new SquareResponse(result);
    }

    private String safeString(String s) {
        return s.replace(Constants.SPACE, Constants.UNDERSCORE).toLowerCase();
    }

    public void postTheMessage(ISquare newSquare, String msg) {
        if (msg.startsWith(Constants.COMMAND_PREFIX)) {
            commandController.processCommand(msg, newSquare);
        } else {
            long currentMillis = System.currentTimeMillis();
            String data = utility.concatStrings(Long.toString(currentMillis), Constants.FILE_DATA_SEPARATOR, msg,
                    Constants.FILE_DATA_SEPARATOR, uniqueId);
            newSquare.addPostMessage(new PostMessage(currentMillis, data));
        }
    }
}

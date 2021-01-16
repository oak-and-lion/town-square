import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class SampleController implements ITextDialogBoxCallback {
    private static final String TILDE = "~";
    private static final String NEWLINE = "\n";
    private static final String INVITATION_LABEL_TEXT = "Invitation:";
    private static final String INVITE_CODE_LABEL = "Invite Code:";
    private static final String POSTS_LABEL = "Posts";
    private static final int TEXTFIELD_WIDTH = 400;
    private static final int POSTS_PANE_WIDTH = 565;
    private static final int POSTS_TEXTFIELD_WIDTH = 515;
    private static final double INVITATION_DIALOG_WIDTH = 550;
    private static final String JOIN_SQUARE_TITLE = "Join Square";
    private static final String JOIN_SQUARE_HEADER_TEXT = "Paste the Invitation";
    private static final String CREATE_SQUARE_TITLE = "Create Square";
    private static final String CREATE_SQUARE_HEADER_TEXT = "Name your new Square";
    private static final String POST_PROMPT_TEXT = "Type your message";
    private static final String POST_BUTTON_TEXT = "Post";
    private static final String EMPTY_STRING = "";
    private static final String DATA_SEPARATOR = "%%%";
    private static final String NO_PASSWORD_VALUE = "~~~~~~~";
    private static final String ZERO = "0";
    private static final String COMMA = ",";
    private static final String TAB_PREFIX = "tab";
    private static final String ENCRYPTION_FLAG = "e";
    private static final String JOIN_COMMAND = "join";
    private static final String MEMBER_COMMAND = "members";
    private static final String REQUEST_PUBLIC_KEY_COMMAND = "pkey";
    private static final String OK_RESULT = "200";
    private static final String ALREADY_REGISTERED_RESULT = "460";
    private static final String MEMBER_FILE_EXT = ".members";
    private static final String SQUARE_FILE_EXT = ".square";
    private static final String POSTS_FILE_EXT = ".posts";
    private static final String IP_FILE = "ip.id";
    private static final String MY_SQUARE_DEFAULT = "my_square";
    private static final String FILE_DATA_SEPARATOR = "~_~";
    private static final String UNDERSCORE = "_";
    private static final String SPACE = " ";
    private static final int JOIN_TYPE = 1;
    private static final int CREATE_TYPE = 2;

    private IApp parent;
    private List<Square> squares;
    private List<String> squareNames;
    private List<String> squareInvites;
    private String publicKey;

    @FXML
    private TextField uniqueId;

    @FXML
    private TextField defaultName;

    @FXML
    private Tab tabDefaultSquare;

    @FXML
    private TabPane tabPane;

    @FXML
    private TextField version;

    @FXML
    private ComboBox<IPAddress> remoteIP;

    @FXML
    private TextField port;

    @FXML
    private MenuItem mnuJoinSquare;

    @FXML
    private void handleSettingsUpdate(ActionEvent event) {
        if (parent != null) {
            parent.sendDefaultName(defaultName.getText());
            parent.sendPort(port.getText());
        }
    }

    @FXML
    private void joinSquare(ActionEvent event) {
        TextDialogBox dialogBox = new TextDialogBox(JOIN_SQUARE_TITLE, JOIN_SQUARE_HEADER_TEXT, EMPTY_STRING, this,
                INVITATION_DIALOG_WIDTH, JOIN_TYPE);
        dialogBox.show();
    }

    @FXML
    private void createSquare(ActionEvent event) {
        TextDialogBox dialogBox = new TextDialogBox(CREATE_SQUARE_TITLE, CREATE_SQUARE_HEADER_TEXT, EMPTY_STRING, this,
                INVITATION_DIALOG_WIDTH, CREATE_TYPE);
        dialogBox.show();
    }

    public void callback(String input, int type) {
        if (type == JOIN_TYPE) {
            processInvitation(input);
        } else if (type == CREATE_TYPE) {
            Utility utility = Utility.create();
            String memberInfo = defaultName.getText() + FILE_DATA_SEPARATOR + publicKey + FILE_DATA_SEPARATOR
                    + remoteIP.getValue().getDisplay() + FILE_DATA_SEPARATOR + port.getText() + FILE_DATA_SEPARATOR
                    + uniqueId.getText();
            utility.writeFile(input.replace(SPACE, UNDERSCORE).toLowerCase() + MEMBER_FILE_EXT, memberInfo);
            // My Square,a7075b5b-b91d-4448-a0f9-d9b0bec1a726,tabDefaultSquare,0,~~~~~~~
            String contents = input + COMMA + utility.createUUID() + COMMA + TAB_PREFIX
                    + input.replace(SPACE, UNDERSCORE).toLowerCase() + COMMA + ZERO + NO_PASSWORD_VALUE;
            setTabSquare(new Square(contents, port.getText(), remoteIP.getValue().getDisplay(),
                    new SquareController(utility, this), utility, this, uniqueId.getText()));
        }
    }

    public SampleController() {
        squares = new ArrayList<Square>();
        squareNames = new ArrayList<String>();
        squareInvites = new ArrayList<String>();
    }

    public void setPublicKey(String key) {
        publicKey = key;
    }

    public void setVersion(String v) {
        if (version != null) {
            version.setText(v);
        }
    }

    public void setRemoteIP(ObservableList<IPAddress> ips, String defaultIp, Utility utility) {
        if (remoteIP != null) {
            remoteIP.setItems(ips);
            int index = 0;
            int count = 0;
            for (IPAddress ip : ips) {
                if (ip.getDisplay().equals(defaultIp)) {
                    index = count;
                    break;
                }
                count++;
            }
            remoteIP.setValue(ips.get(index));

            remoteIP.valueProperty().addListener((obs, oldValue, newValue) -> {
                utility.writeFile(IP_FILE, newValue.getDisplay());
                for (Square square : squares) {
                    square.setIP(newValue.getDisplay());
                    String invite = buildInviteCode(square, determineSquarePrivacy(square));
                    ((TextField) square.getTemp()).setText(invite);
                }
            });
            remoteIP.setConverter(new StringConverter<IPAddress>() {
                @Override
                public String toString(IPAddress object) {
                    return object.getDisplay();
                }

                @Override
                public IPAddress fromString(String string) {
                    return new IPAddress(string, string);
                }
            });
        }
    }

    public void setPort(String portNum) {
        if (port != null) {
            port.setText(portNum);
        }
    }

    public void setParent(IApp p) {
        parent = p;
    }

    public void setUniqueId(String msg) {
        if (uniqueId != null) {
            uniqueId.setText(msg);
        }
    }

    public void setDefaultName(String msg) {
        if (defaultName != null) {
            defaultName.setText(msg);
        }
    }

    public void setTabSquare(Square square) {
        if (!squares.contains(square)) {
            squares.add(square);
            squareNames.add(square.getName());
            squareInvites.add(square.getInvite());
            createTab(square, squares.size() - 1);
        }
    }

    private void createTab(Square square, int index) {
        VBox main = new VBox();
        Tab tab = new Tab();
        tab.setText(square.getName());
        tab.setId(square.getId());

        // invite code
        HBox inviteControls = createInviteControls(square);
        main.getChildren().add(inviteControls);

        // invite controls
        HBox generateInviteControls = createGenerateInviteControls(square, index);
        main.getChildren().add(generateInviteControls);

        VBox generatePostControls = createGeneratePostControls(square);
        main.getChildren().add(generatePostControls);

        // add main
        tab.setContent(main);
        tabPane.getTabs().add(0, tab);
    }

    private VBox createGeneratePostControls(Square square) {
        VBox generatePostControls = createVBox(0, 10, 0, 10);
        generatePostControls.setMinHeight(281);
        generatePostControls.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
                + "-fx-border-insets: 5;" + "-fx-border-radius: 5;" + "-fx-border-color: #333;");

        HBox postsLabelHBox = createHBox(0, 0, 0, 0);
        Label postsLabel = createLabel(POSTS_LABEL, 0, 0, 0, 0);
        postsLabelHBox.getChildren().add(postsLabel);

        HBox postsHBox = createHBox(10, 0, 10, 0);

        VBox postsList = createVBox(5, 5, 5, 5);
        ScrollPane postsPane = createPostPane(postsList);

        postsHBox.getChildren().add(postsPane);

        square.setPostsScrollPane(postsPane);
        square.setPostsVBox(postsList);

        HBox postsButtonHBox = createHBox(0, 0, 7, 0);

        TextField postsTextField = createTextField(EMPTY_STRING, POST_PROMPT_TEXT, true, POSTS_TEXTFIELD_WIDTH);

        TownSquareButton postsButton = new TownSquareButton(POST_BUTTON_TEXT, square, postsTextField);
        postsButton.setOnAction(event -> {
            Utility utility = Utility.create();
            Square newSquare = postsButton.getSquare();
            long currentMillis = System.currentTimeMillis();
            String data = Long.toString(currentMillis) + FILE_DATA_SEPARATOR + postsButton.getPostMessage()
                    + FILE_DATA_SEPARATOR + uniqueId.getText();
            utility.appendToFile(newSquare.getSafeLowerName() + POSTS_FILE_EXT, NEWLINE + data);
            postsButton.clearPostMessage();
        });

        postsTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                postsButton.fire();
            }
        });

        Label spacer = createLabel(EMPTY_STRING, 0, 5, 0, 5);

        postsButtonHBox.getChildren().addAll(postsButton, spacer, postsTextField);

        generatePostControls.getChildren().addAll(postsLabelHBox, postsHBox, postsButtonHBox);

        return generatePostControls;
    }

    private ScrollPane createPostPane(VBox postsList) {
        ScrollPane postsPane = new ScrollPane(postsList);
        postsPane.setMinHeight(190);
        postsPane.setMinWidth(POSTS_PANE_WIDTH);

        return postsPane;
    }

    private HBox createGenerateInviteControls(Square square, int index) {
        HBox generateInviteControls = createHBox(0, 10, 10, 10);

        Label invitation = createLabel(INVITATION_LABEL_TEXT, 0, 5, 0, 10);

        TextField generateInvite = createInviteTextField(square);

        generateInviteControls.getChildren().addAll(invitation, generateInvite);

        // update square for event reference
        square.setTemp(generateInvite);
        squares.set(index, square);

        return generateInviteControls;
    }

    private Label createLabel(String text, int top, int right, int bottom, int left) {
        Label invitation = new Label(text);
        invitation.setPadding(new Insets(top, right, bottom, left));
        return invitation;
    }

    private HBox createHBox(int top, int right, int bottom, int left) {
        HBox result = new HBox();
        result.setPadding(new Insets(top, right, bottom, left));
        return result;
    }

    private VBox createVBox(int top, int right, int bottom, int left) {
        VBox result = new VBox();
        result.setPadding(new Insets(top, right, bottom, left));
        return result;
    }

    private TextField createInviteTextField(Square square) {
        TextField generateInvite = new TextField(buildInviteCode(square, determineSquarePrivacy(square)));
        generateInvite.setMinWidth(TEXTFIELD_WIDTH);
        generateInvite.setEditable(false);

        return generateInvite;
    }

    private TextField createTextField(String text, String promptText, boolean editable, int minWidth) {
        TextField result = new TextField(text);
        result.setEditable(editable);
        result.setPromptText(promptText);
        if (minWidth > 0) {
            result.setMinWidth(minWidth);
        }
        return result;
    }

    private HBox createInviteControls(Square square) {
        HBox inviteControls = createHBox(10, 10, 10, 10);

        Label inviteLabel = createLabel(INVITE_CODE_LABEL, 5, 5, 0, 0);

        TextField inviteCodeLabel = createTextField(square.getInvite(), EMPTY_STRING, false, TEXTFIELD_WIDTH);

        inviteControls.getChildren().addAll(inviteLabel, inviteCodeLabel);

        return inviteControls;
    }

    private String buildInviteCode(Square square, String encrypt) {
        return encrypt + TILDE + square.getIP() + TILDE + square.getPort() + TILDE + square.getInvite();
    }

    private String determineSquarePrivacy(Square square) {
        if (square != null) {
            return ENCRYPTION_FLAG;
        }

        return ENCRYPTION_FLAG;
    }

    public Square getSquareByInvite(String id) {
        if (squareInvites.contains(id)) {
            int index = squareInvites.indexOf(id);
            return squares.get(index);
        }

        return null;
    }

    private String safeString(String s) {
        return s.replace(SPACE, UNDERSCORE).toLowerCase();
    }

    public String getDefaultName() {
        return parent.getDefaultName();
    }

    public void processInvitation(String invite) {
        // sample invitation
        // 0 == encryption flag
        // 1 == ip of the remote host to process invitation
        // 2 == port of remote host
        // 3 == id of the square being invited to
        // u~207.244.84.59~44123~a7075b5b-b91d-4448-a0f9-d9b0bec1a726
        String[] split = invite.split(TILDE);
        Client client = new Client(split[1], Integer.valueOf(split[2]), split[3]);
        boolean encrypt = false;
        SquareKeyPair tempKeys = new SquareKeyPair();

        if (split[0].equals(ENCRYPTION_FLAG)) {
            encrypt = true;
        }

        String data = JOIN_COMMAND + DATA_SEPARATOR + defaultName.getText() + DATA_SEPARATOR + publicKey
                + DATA_SEPARATOR + remoteIP.getValue().getDisplay() + DATA_SEPARATOR + port.getText() + DATA_SEPARATOR
                + uniqueId.getText();

        if (encrypt) {
            String remotePublicKey = client.sendMessage(REQUEST_PUBLIC_KEY_COMMAND, false);
            SquareResponse response = processTCPReturn(remotePublicKey);
            if (!response.getCode().equals(OK_RESULT)) {
                return;
            }

            tempKeys.setPublicKeyFromBase64(response.getMessage());

            Utility utility = Utility.create();
            String password = utility.generateRandomString(16);
            StringBuilder temp = new StringBuilder();
            temp.append(utility.encrypt(data, password));
            data = tempKeys.encryptToBase64(password) + DATA_SEPARATOR + temp.toString();
        }

        SquareResponse response = processTCPReturn(client.sendMessage(data, encrypt));

        if (response.getCode().equals(OK_RESULT) || response.getCode().equals(ALREADY_REGISTERED_RESULT)) {
            String[] responseData = response.getResponseSplit();
            String temp = MEMBER_COMMAND + DATA_SEPARATOR + uniqueId.getText();
            Utility utility = Utility.create();
            String password = utility.generateRandomString(16);
            data = tempKeys.encryptToBase64(password) + DATA_SEPARATOR + utility.encrypt(temp, password);
            response = processTCPReturn(client.sendMessage(data, encrypt));
            String squareSafeName = safeString(responseData[3]);
            utility.writeFile(squareSafeName + MEMBER_FILE_EXT, response.getMessage().replace(DATA_SEPARATOR, NEWLINE));
            String info = responseData[3] + COMMA + client.getSquareId() + COMMA + TAB_PREFIX + squareSafeName + COMMA
                    + ZERO + NO_PASSWORD_VALUE;
            Square square = new Square(info, port.getText(), remoteIP.getValue().getDisplay(),
                    new SquareController(utility, this), utility, this, uniqueId.getText());
            utility.writeFile(squareSafeName + SQUARE_FILE_EXT, info);
            setTabSquare(square);
        }
    }

    public void buildSquares(Utility utility) {
        String[] files = utility.getFiles(SQUARE_FILE_EXT);
        for (String file : files) {
            if (file.equals(MY_SQUARE_DEFAULT + SQUARE_FILE_EXT)) {
                continue;
            }
            String contents = utility.readFile(file);
            setTabSquare(new Square(contents, port.getText(), remoteIP.getValue().getDisplay(),
                    new SquareController(utility, this), utility, this, uniqueId.getText()));
        }
    }

    private SquareResponse processTCPReturn(String result) {
        return new SquareResponse(result);
    }

    public void addPostMessages(VBox messageList, ScrollPane scrollPane, String message) {
        Label label = new Label();
        label.setText(message);
        messageList.getChildren().addAll(label);
        scrollPane.setVvalue(Double.MIN_VALUE);
        scrollPane.setVvalue(Double.MAX_VALUE);
    }
}

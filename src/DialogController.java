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

public class DialogController implements ITextDialogBoxCallback, IDialogController {
    private IApp parent;
    private List<ISquare> squares;
    private List<String> squareNames;
    private List<String> squareInvites;
    private String publicKey;
    private IUtility utility;

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
        ITextDialogBox dialogBox = Factory.creaTextDialogBox(Constants.BASE_TEXT_DIALOG_BOX,
                Constants.JOIN_SQUARE_TITLE, Constants.JOIN_SQUARE_HEADER_TEXT, Constants.EMPTY_STRING, this,
                Constants.INVITATION_DIALOG_WIDTH, Constants.JOIN_TYPE);
        dialogBox.show();
    }

    @FXML
    private void createSquare(ActionEvent event) {
        ITextDialogBox dialogBox = Factory.creaTextDialogBox(Constants.BASE_TEXT_DIALOG_BOX,
                Constants.CREATE_SQUARE_TITLE, Constants.CREATE_SQUARE_HEADER_TEXT, Constants.EMPTY_STRING, this,
                Constants.INVITATION_DIALOG_WIDTH, Constants.CREATE_TYPE);
        dialogBox.show();
    }

    public void callback(String input, int type) {
        if (type == Constants.JOIN_TYPE) {
            processInvitation(input);
        } else if (type == Constants.CREATE_TYPE) {
            processCreateSquare(input);
        }
    }

    public DialogController() {
        squares = new ArrayList<ISquare>();
        squareNames = new ArrayList<String>();
        squareInvites = new ArrayList<String>();
    }

    public void setUtilityController(IUtility utilityController) {
        utility = utilityController;
    }

    public void setPublicKey(String key) {
        publicKey = key;
    }

    public void setVersion(String v) {
        if (version != null) {
            version.setText(v);
        }
    }

    public void setRemoteIP(ObservableList<IPAddress> ips, String defaultIp) {
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
                utility.writeFile(Constants.IP_FILE, newValue.getDisplay());
                for (ISquare square : squares) {
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

    public void setTabSquare(ISquare square) {
        if (!squares.contains(square)) {
            squares.add(square);
            squareNames.add(square.getName());
            squareInvites.add(square.getInvite());
            createTab(square, squares.size() - 1);
        }
    }

    private void createTab(ISquare square, int index) {
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

    private VBox createGeneratePostControls(ISquare square) {
        VBox generatePostControls = createVBox(0, 10, 0, 10);
        generatePostControls.setMinHeight(281);
        generatePostControls.setStyle("-fx-padding: 10;-fx-border-style: solid inside;-fx-border-width: 2;"
                + "-fx-border-insets: 5;-fx-border-radius: 5;-fx-border-color: #333;");

        HBox postsLabelHBox = createHBox(0, 0, 0, 0);
        Label postsLabel = createLabel(Constants.POSTS_LABEL, 0, 0, 0, 0);
        postsLabelHBox.getChildren().add(postsLabel);

        HBox postsHBox = createHBox(10, 0, 10, 0);

        VBox postsList = createVBox(5, 5, 5, 5);
        ScrollPane postsPane = createPostPane(postsList);

        postsHBox.getChildren().add(postsPane);

        square.setPostsScrollPane(postsPane);
        square.setPostsVBox(postsList);

        HBox postsButtonHBox = createHBox(0, 0, 7, 0);

        TextField postsTextField = createTextField(Constants.EMPTY_STRING, Constants.POST_PROMPT_TEXT, true,
                Constants.POSTS_TEXTFIELD_WIDTH);

        TownSquareButton postsButton = Factory.createTownSquareButton(Constants.BASE_TOWN_SQUARE_BUTTON,
                Constants.POST_BUTTON_TEXT, square, postsTextField);
        postsButton.setOnAction(event -> {
            ISquare newSquare = postsButton.getSquare();
            long currentMillis = System.currentTimeMillis();
            String data = Long.toString(currentMillis) + Constants.FILE_DATA_SEPARATOR + postsButton.getPostMessage()
                    + Constants.FILE_DATA_SEPARATOR + uniqueId.getText();
            String postsFile = newSquare.getSafeLowerName() + Constants.POSTS_FILE_EXT;
            if (utility.checkFileExists(postsFile)) {
                utility.appendToFile(postsFile, Constants.NEWLINE + data);
            } else {
                utility.writeFile(postsFile, data);
            }
            postsButton.clearPostMessage();
        });

        postsTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                postsButton.fire();
            }
        });

        Label spacer = createLabel(Constants.EMPTY_STRING, 0, 5, 0, 5);

        postsButtonHBox.getChildren().addAll(postsButton, spacer, postsTextField);

        generatePostControls.getChildren().addAll(postsLabelHBox, postsHBox, postsButtonHBox);

        return generatePostControls;
    }

    private ScrollPane createPostPane(VBox postsList) {
        ScrollPane postsPane = new ScrollPane(postsList);
        postsPane.setMinHeight(190);
        postsPane.setMinWidth(Constants.POSTS_PANE_WIDTH);

        return postsPane;
    }

    private HBox createGenerateInviteControls(ISquare square, int index) {
        HBox generateInviteControls = createHBox(0, 10, 10, 10);

        Label invitation = createLabel(Constants.INVITATION_LABEL_TEXT, 0, 5, 0, 10);

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

    private TextField createInviteTextField(ISquare square) {
        TextField generateInvite = new TextField(buildInviteCode(square, determineSquarePrivacy(square)));
        generateInvite.setMinWidth(Constants.TEXTFIELD_WIDTH);
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

    private HBox createInviteControls(ISquare square) {
        HBox inviteControls = createHBox(10, 10, 10, 10);

        Label inviteLabel = createLabel(Constants.INVITE_CODE_LABEL, 5, 5, 0, 0);

        TextField inviteCodeLabel = createTextField(square.getInvite(), Constants.EMPTY_STRING, false,
                Constants.TEXTFIELD_WIDTH);

        inviteControls.getChildren().addAll(inviteLabel, inviteCodeLabel);

        return inviteControls;
    }

    private String buildInviteCode(ISquare square, String encrypt) {
        return encrypt + Constants.TILDE + square.getIP() + Constants.TILDE + square.getPort() + Constants.TILDE
                + square.getInvite();
    }

    private String determineSquarePrivacy(ISquare square) {
        if (square != null) {
            return Constants.ENCRYPTION_FLAG;
        }

        return Constants.ENCRYPTION_FLAG;
    }

    public ISquare getSquareByInvite(String id) {
        if (squareInvites.contains(id)) {
            int index = squareInvites.indexOf(id);
            return squares.get(index);
        }

        return null;
    }

    private String safeString(String s) {
        return s.replace(Constants.SPACE, Constants.UNDERSCORE).toLowerCase();
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
        String[] split = invite.split(Constants.TILDE);
        IClient client = Factory.createClient(Constants.BASE_CLIENT, split[1], Integer.valueOf(split[2]), split[3]);
        boolean encrypt = false;
        ISquareKeyPair tempKeys = Factory.createSquareKeyPair(Constants.BASE_SQUARE_KEY_PAIR);

        if (split[0].equals(Constants.ENCRYPTION_FLAG)) {
            encrypt = true;
        }

        String data = Constants.JOIN_COMMAND + Constants.COMMAND_DATA_SEPARATOR + defaultName.getText()
                + Constants.COMMAND_DATA_SEPARATOR + publicKey + Constants.COMMAND_DATA_SEPARATOR
                + remoteIP.getValue().getDisplay() + Constants.COMMAND_DATA_SEPARATOR + port.getText()
                + Constants.COMMAND_DATA_SEPARATOR + uniqueId.getText();

        if (encrypt) {
            String remotePublicKey = client.sendMessage(Constants.REQUEST_PUBLIC_KEY_COMMAND, false);
            SquareResponse response = processTCPReturn(remotePublicKey);
            if (!response.getCode().equals(Constants.OK_RESULT)) {
                return;
            }

            tempKeys.setPublicKeyFromBase64(response.getMessage());

            String password = utility.generateRandomString(16);
            StringBuilder temp = new StringBuilder();
            temp.append(utility.encrypt(data, password));
            data = tempKeys.encryptToBase64(password) + Constants.COMMAND_DATA_SEPARATOR + temp.toString();
        }

        SquareResponse response = processTCPReturn(client.sendMessage(data, encrypt));

        if (response.getCode().equals(Constants.OK_RESULT)
                || response.getCode().equals(Constants.ALREADY_REGISTERED_RESULT)) {
            String[] responseData = response.getResponseSplit();
            String temp = Constants.MEMBER_COMMAND + Constants.COMMAND_DATA_SEPARATOR + uniqueId.getText();
            String password = utility.generateRandomString(16);
            data = tempKeys.encryptToBase64(password) + Constants.COMMAND_DATA_SEPARATOR
                    + utility.encrypt(temp, password);
            response = processTCPReturn(client.sendMessage(data, encrypt));
            String squareSafeName = safeString(responseData[3]);
            utility.writeFile(squareSafeName + Constants.MEMBERS_FILE_EXT,
                    response.getMessage().replace(Constants.COMMAND_DATA_SEPARATOR, Constants.NEWLINE));
            String info = responseData[3] + Constants.COMMA + client.getSquareId() + Constants.COMMA
                    + Constants.TAB_PREFIX + squareSafeName + Constants.COMMA + Constants.ZERO
                    + Constants.NO_PASSWORD_VALUE;
            ISquare square = Factory.createSquare(Constants.BASE_SQUARE, info, port.getText(),
                    remoteIP.getValue().getDisplay(),
                    Factory.createSquareController(Constants.BASE_SQUARE_CONTROLLER, utility, this,
                            Factory.createLogger(Constants.FILE_LOGGER, uniqueId.getText() + ".log", utility)),
                    utility, this, uniqueId.getText());
            utility.writeFile(squareSafeName + Constants.SQUARE_FILE_EXT, info);
            setTabSquare(square);
        }
    }

    public void buildSquares() {
        String[] files = utility.getFiles(Constants.SQUARE_FILE_EXT);
        for (String file : files) {
            if (file.equals(Constants.MY_SQUARE_DEFAULT + Constants.SQUARE_FILE_EXT)) {
                continue;
            }
            String contents = utility.readFile(file);
            setTabSquare(new Square(contents, port.getText(), remoteIP.getValue().getDisplay(),
                    Factory.createSquareController(Constants.BASE_SQUARE_CONTROLLER, utility, this,
                            Factory.createLogger(Constants.FILE_LOGGER, uniqueId.getText() + ".log", utility)),
                    utility, this, uniqueId.getText()));
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

    private void processCreateSquare(String input) {
        String memberInfo = defaultName.getText() + Constants.FILE_DATA_SEPARATOR + publicKey
                + Constants.FILE_DATA_SEPARATOR + remoteIP.getValue().getDisplay() + Constants.FILE_DATA_SEPARATOR
                + port.getText() + Constants.FILE_DATA_SEPARATOR + uniqueId.getText();
        utility.writeFile(
                input.replace(Constants.SPACE, Constants.UNDERSCORE).toLowerCase() + Constants.MEMBERS_FILE_EXT,
                memberInfo);
        // My Square,a7075b5b-b91d-4448-a0f9-d9b0bec1a726,tabDefaultSquare,0,~~~~~~~
        String uuid = utility.createUUID();
        String safeName = input.replace(Constants.SPACE, Constants.UNDERSCORE).toLowerCase();
        String contents = input + Constants.COMMA + uuid + Constants.COMMA + Constants.TAB_PREFIX + safeName
                + Constants.COMMA + Constants.ZERO + Constants.NO_PASSWORD_VALUE;
        utility.writeFile(safeName + Constants.SQUARE_FILE_EXT, contents);
        setTabSquare(new Square(contents, port.getText(), remoteIP.getValue().getDisplay(),
                Factory.createSquareController(Constants.BASE_SQUARE_CONTROLLER, utility, this,
                        Factory.createLogger(Constants.FILE_LOGGER, uniqueId.getText() + ".log", utility)),
                utility, this, uniqueId.getText()));
    }
}

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class DialogController implements ITextDialogBoxCallback, IDialogController {
    private IApp parent;
    private List<ISquare> squares;
    private List<String> squareNames;
    private List<String> squareInvites;
    private String publicKey;
    private IUtility utility;
    private Stage primaryStage;
    private ArrayList<VBox> postControls;
    private ArrayList<ScrollPane> postScrollPanes;
    private ArrayList<TextField> postTextFields;
    private ArrayList<MessageWorker> postMessageWorkers;
    private ArrayList<Long> knownPostMessages;
    private ArrayList<ImageView> images;
    private ArrayList<MediaView> videos;
    private IModalViewer modalImageViewer;
    private IModalViewer modalVideoViewer;
    private ICommandController commandController;
    private IFactory factory;

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
    private MenuItem mnuCreateSquare;

    @FXML
    private MenuItem mnuAttachImage;

    @FXML
    private MenuItem mnuAttachFile;

    @FXML
    private MenuItem mnuLinkUrl;

    @FXML
    private TextField alias;

    @FXML
    private MenuItem mnuAbout;

    @FXML
    private MenuItem mnuClose;

    @FXML
    private MenuItem mnuLicense;

    @FXML
    private void handleSettingsUpdate(ActionEvent event) {
        if (parent != null) {
            parent.sendDefaultName(defaultName.getText());
            parent.sendPort(port.getText());
            updateDefaultNameInMemberFiles(defaultName.getText());
            updatePortInMemberFiles(port.getText(), uniqueId.getText());
            updateAliases(alias.getText());
            updateInvitations(remoteIP.getSelectionModel().getSelectedItem().getDisplay(), port.getText());
        }
    }

    @FXML
    private void joinSquare(ActionEvent event) {
        ITextDialogBox dialogBox = factory.createTextDialogBox(Constants.BASE_TEXT_DIALOG_BOX,
                Constants.JOIN_SQUARE_TITLE, Constants.JOIN_SQUARE_HEADER_TEXT, Constants.EMPTY_STRING, this,
                Constants.INVITATION_DIALOG_WIDTH, Constants.JOIN_TYPE);
        dialogBox.show();
    }

    @FXML
    private void createSquare(ActionEvent event) {
        ITextDialogBox dialogBox = factory.createTextDialogBox(Constants.BASE_TEXT_DIALOG_BOX,
                Constants.CREATE_SQUARE_TITLE, Constants.CREATE_SQUARE_HEADER_TEXT, Constants.EMPTY_STRING, this,
                Constants.INVITATION_DIALOG_WIDTH, Constants.CREATE_TYPE);
        dialogBox.show();
    }

    @FXML
    private void leaveSquare(ActionEvent event) {
        IAlertBox alertBox = factory.createAlertBox(Constants.BASE_ALERT_BOX);
        IAlert alert = alertBox.createAlert(Constants.LEAVE_SQUARE_TITLE, Constants.LEAVE_SQUARE_HEADER,
                Constants.LEAVE_SQUARE_CONTENT, AlertType.CONFIRMATION);
        ButtonType bt = alert.getSelectedButton();
        if (bt.equals(ButtonType.OK)) {
            ISquare square = (ISquare) tabPane.getSelectionModel().getSelectedItem().getUserData();
            if (square == null || square.getTrueName().equals(Constants.DEFAULT_SQUARE_NAME)) {
                return;
            }
            utility.writeFile(utility.concatStrings(square.getSafeLowerName(), Constants.PAUSE_FILE_EXT),
                    Constants.PAUSE_FILE_CONTENTS);
            utility.writeFile(utility.concatStrings(square.getSafeLowerName(), Constants.LEAVE_FILE_EXT),
                    Constants.LEAVE_FILE_CONTENTS);
            utility.deleteFile(utility.concatStrings(square.getSafeLowerName(), Constants.MEMBERS_FILE_EXT));
            utility.deleteFile(utility.concatStrings(square.getSafeLowerName(), Constants.POSTS_FILE_EXT));
            utility.writeFile(utility.concatStrings(square.getSafeLowerName(), Constants.MEMBERS_FILE_EXT),
                    utility.concatStrings(Constants.EXIT_SQUARE_TEXT, Constants.FILE_DATA_SEPARATOR,
                            Constants.NULL_TEXT, Constants.FILE_DATA_SEPARATOR, Constants.NULL_TEXT,
                            Constants.FILE_DATA_SEPARATOR, Constants.NULL_TEXT, Constants.FILE_DATA_SEPARATOR,
                            uniqueId.getText()));
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            tabPane.getTabs().remove(tab);
        }
    }

    @FXML
    private void attachImage(ActionEvent event) {
        // attach the image
        attachAttachment(Constants.IMAGE_DIALOG_TITLE, Constants.IMAGE_MARKER);
    }

    @FXML
    private void attachVideo(ActionEvent event) {
        attachAttachment(Constants.VIDEO_DIALOG_TITLE, Constants.VIDEO_MARKER);
    }

    @FXML
    private void attachFile(ActionEvent event) {
        attachAttachment(Constants.FILE_DIALOG_TITLE, Constants.FILE_MARKER);
    }

    @FXML
    private void linkUrl(ActionEvent event) {
        // link url
        ITextDialogBox dialog = factory.createTextDialogBox(Constants.BASE_TEXT_DIALOG_BOX, Constants.URL_HEADER,
                Constants.URL_CONTENT, Constants.EMPTY_STRING, this, Constants.INVITATION_DIALOG_WIDTH,
                Constants.LINK_URL_TYPE);
        dialog.show();
    }

    @FXML
    private void showAbout(ActionEvent event) {
        showAbout();
    }

    @FXML
    private void showCommands(ActionEvent event) {
        commandController.processCommand(utility.concatStrings(Constants.FORWARD_SLASH, Constants.HELP_COMMAND), null);
    }

    @FXML
    private void showLicense(ActionEvent event) {
        showLicense();
    }

    @FXML
    private void handleClose(ActionEvent event) {
        parent.closeApp(Constants.SYSTEM_EXIT_OK, Constants.GRACEFUL_SHUTDOWN);
    }

    public void showLicense() {
        ModalLicenseViewer l = new ModalLicenseViewer();
        l.show(License.getLicense(utility));
    }

    public void showAbout() {
        IAlertBox alertBox = factory.createAlertBox(Constants.BASE_ALERT_BOX);
        alertBox.createAlert(Constants.ABOUT_TITLE, Constants.ABOUT_HEADER,
                utility.concatStrings(Constants.VERSION_TEXT_PREFIX, Constants.VERSION, Constants.NEWLINE,
                        Constants.DEVELOPED_BY, Constants.DEVELOPER_ONE_NAME, Constants.NEWLINE, Constants.GITHUB_REPO),
                AlertType.INFORMATION);
    }

    public void showCloneMessage() {
        IAlertBox alertBox = factory.createAlertBox(Constants.BASE_ALERT_BOX);
        alertBox.createAlert(Constants.CLONE_MESSAGE_TITLE, Constants.CLONE_MESSAGE_HEADER, Constants.CLONE_MESSAGE,
                AlertType.INFORMATION);
    }

    public void showList(String[] items, String listTitle, String listHeader) {
        IAlertBox alertBox = factory.createAlertBox(Constants.BASE_ALERT_BOX);
        alertBox.createAlert(listTitle, listHeader, String.join(Constants.NEWLINE, items), AlertType.INFORMATION);
    }

    private void attachAttachment(String dialogTitle, String marker) {
        ISquare square = (ISquare) tabPane.getSelectionModel().getSelectedItem().getUserData();
        if (square == null) {
            return;
        }
        FileChooser fc = new FileChooser();
        fc.setTitle(dialogTitle);
        File file = fc.showOpenDialog(primaryStage);
        if (file == null) {
            return;
        }
        File target = new File(file.getName());
        try {
            if (!utility.checkFileExists(target.getName())) {
                Files.copy(file.toPath(), target.toPath());
            }
            postTheMessage(square, utility.concatStrings(marker, target.getName()));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void callback(String input, int type) {
        if (type == Constants.JOIN_TYPE) {
            processInvitation(input);
        } else if (type == Constants.CREATE_TYPE) {
            processCreateSquare(input);
        } else if (type == Constants.LINK_URL_TYPE) {
            processLinkUrl(input);
        }
    }

    public void processLinkUrl(String input) {
        ISquare square = (ISquare) tabPane.getSelectionModel().getSelectedItem().getUserData();
        if (square == null) {
            return;
        }

        postTheMessage(square, utility.concatStrings(Constants.FILE_MARKER, input));
    }

    public void updatePauseNotification(ISquare square, boolean paused) {
        Tab tab = square.getTab();

        if (paused) {
            tab.setText(utility.concatStrings(tab.getText(), Constants.PAUSED_TAB_NOTIFICATION));
        } else {
            tab.setText(tab.getText().replace(Constants.PAUSED_TAB_NOTIFICATION, Constants.EMPTY_STRING));
        }
    }

    public void setTabName(ISquare square, String oldName, String newName) {
        Tab tab = square.getTab();
        tab.setText(tab.getText().replace(oldName, newName));
    }

    public DialogController() {
        squares = new ArrayList<>();
        squareNames = new ArrayList<>();
        squareInvites = new ArrayList<>();
    }

    public boolean isGui() {
        return true;
    }

    public IApp getParent() {
        return parent;
    }

    public void setStage(Stage stage) {
        primaryStage = stage;
    }

    public void setCommandController(ICommandController value) {
        commandController = value;
    }

    public ICommandController getCommandController() {
        return commandController;
    }

    public void setFactory(IFactory value) {
        factory = value;
    }

    public void resizeControls(double width, double height) {
        tabPane.setMinWidth(width - Constants.TAB_PANE_WIDTH_DIFF);
        tabPane.setMinHeight(height - Constants.TAB_PANE_HEIGHT_DIFF);
        for (VBox box : postControls) {
            box.setMinWidth(width - Constants.POSTS_BOX_WIDTH_DIFF);
        }
        for (ScrollPane pane : postScrollPanes) {
            pane.setMinWidth(width - Constants.POSTS_BOX_WIDTH_DIFF);
        }
        for (TextField field : postTextFields) {
            field.setMinWidth(width - Constants.POSTS_TEXT_FIELD_WIDTH_DIFF);
        }
        if (postMessageWorkers != null) {
            for (MessageWorker worker : postMessageWorkers) {
                worker.recalculateWidth();
            }
        }
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
                parent.sendIP(newValue.getDisplay(), oldValue.getDisplay(), uniqueId.getText());
                updateInvitations(newValue.getDisplay(), port.getText());
                updateIPAddressInMemberFiles(newValue.getDisplay(), uniqueId.getText());
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

    private void updateInvitations(String ip, String port) {
        for (ISquare square : squares) {
            square.setIP(ip);
            square.setPort(port);
            String invite = buildInviteCode(square, determineSquarePrivacy(square));
            ((TextField) square.getTemp()).setText(invite);
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

    public void setAlias(String s) {
        if (alias != null) {
            alias.setText(s);
        }
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
            if (!utility.checkFileExists(utility.concatStrings(square.getSafeLowerName(), Constants.LEAVE_FILE_EXT))) {
                createTab(square, squares.size() - 1);
            }
        }
    }

    private void createTab(ISquare square, int index) {
        VBox main = new VBox();
        Tab tab = new Tab();
        tab.setText(square.getName());
        if (utility.checkFileExists(utility.concatStrings(square.getSafeLowerName(), Constants.PAUSE_FILE_EXT))) {
            tab.setText(utility.concatStrings(tab.getText(), Constants.PAUSED_TAB_NOTIFICATION));
        }
        tab.setId(square.getId());
        tab.setUserData(square);

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
        square.setTab(tab);
    }

    private VBox createGeneratePostControls(ISquare square) {
        Label spacer = createLabel(Constants.EMPTY_STRING, 0, 5, 0, 5);
        VBox generatePostControls = createVBox(0, 10, 0, 10);
        generatePostControls.setMinHeight(281);
        generatePostControls.setStyle(utility.concatStrings("-fx-padding: 10;-fx-border-style: solid inside;-fx-border-width: 2;"
                , "-fx-border-insets: 5;-fx-border-radius: 5;-fx-border-color: #333;"));

        if (postControls == null) {
            postControls = new ArrayList<>();
        }

        postControls.add(generatePostControls);

        HBox postsLabelHBox = createHBox(0, 0, 0, 0);
        Label postsLabel = createLabel(Constants.POSTS_LABEL, 5, 5, 0, 0);
        ITownSquareButton membersButton = factory.createTownSquareButton(Constants.BASE_TOWN_SQUARE_BUTTON,
                Constants.MEMBER_BUTTON_TEXT, square, null);
        membersButton.setOnAction(event -> {
            IShowSquareMembers showMembers = factory.createShowSquareMembers(Constants.BASE_SHOW_SQUARE_MEMBERS);
            showMembers.showMembers(membersButton.getSquare(), factory, utility);
        });
        postsLabelHBox.getChildren().addAll(postsLabel, (TownSquareButton) membersButton);

        HBox postsHBox = createHBox(10, 0, 10, 0);

        VBox postsList = createVBox(5, 5, 5, 5);
        ScrollPane postsPane = createPostPane(postsList);

        if (postScrollPanes == null) {
            postScrollPanes = new ArrayList<>();
        }

        postScrollPanes.add(postsPane);

        postsHBox.getChildren().add(postsPane);

        square.setPostsScrollPane(postsPane);
        square.setPostsVBox(postsList);

        HBox postsButtonHBox = createHBox(0, 0, 7, 0);

        TextField postsTextField = createTextField(Constants.EMPTY_STRING, Constants.POST_PROMPT_TEXT, true,
                Constants.POSTS_TEXTFIELD_WIDTH);

        ITownSquareButton postsButton = factory.createTownSquareButton(Constants.BASE_TOWN_SQUARE_BUTTON,
                Constants.POST_BUTTON_TEXT, square, postsTextField);
        postsButton.setOnAction(event -> {
            ISquare newSquare = postsButton.getSquare();
            postTheMessage(newSquare, postsButton.getPostMessage());
            postsButton.clearPostMessage();
        });

        postsTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                postsButton.fire();
            }
        });

        if (postTextFields == null) {
            postTextFields = new ArrayList<>();
        }

        postTextFields.add(postsTextField);

        postsButtonHBox.getChildren().addAll((TownSquareButton) postsButton, spacer, postsTextField);

        generatePostControls.getChildren().addAll(postsLabelHBox, postsHBox, postsButtonHBox);

        return generatePostControls;
    }

    private void postTheMessage(ISquare newSquare, String msg) {
        if (newSquare == null) {
            System.out.println("no square to post to");
            return;
        }
        if (msg.startsWith(Constants.COMMAND_PREFIX)) {
            commandController.processCommand(msg, newSquare);
        } else {
            long currentMillis = System.currentTimeMillis();
            String data = utility.concatStrings(Long.toString(currentMillis), Constants.FILE_DATA_SEPARATOR, msg,
                    Constants.FILE_DATA_SEPARATOR, uniqueId.getText());
            newSquare.addPostMessage(new PostMessage(currentMillis, data));
        }
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
        return utility.concatStrings(encrypt, Constants.TILDE, square.getIP(), Constants.TILDE, square.getPort(),
                Constants.TILDE, square.getInvite());
    }

    private String determineSquarePrivacy(ISquare square) {
        return Constants.ENCRYPTED_FLAG;
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

    public boolean processInvitation(String invite) {
        // sample invitation
        // 0 == encryption flag
        // 1 == ip of the remote host to process invitation
        // 2 == port of remote host
        // 3 == id of the square being invited to
        // u~207.244.84.59~44123~a7075b5b-b91d-4448-a0f9-d9b0bec1a726
        String[] split = invite.split(Constants.TILDE);
        IClient client = factory.createClient(Constants.BASE_CLIENT, split[1], Integer.valueOf(split[2]), split[3]);
        boolean encrypt = false;
        ISquareKeyPair tempKeys = factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility);

        if (split[0].equals(Constants.ENCRYPTED_FLAG)) {
            encrypt = true;
        }

        String remotePublicKey = client.sendMessage(Constants.REQUEST_PUBLIC_KEY_COMMAND, Constants.DO_NOT_ENCRYPT_CLIENT_TRANSFER);
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
                defaultName.getText(), Constants.COMMAND_DATA_SEPARATOR, publicKey, Constants.COMMAND_DATA_SEPARATOR,
                remoteIP.getValue().getDisplay(), Constants.COMMAND_DATA_SEPARATOR, port.getText(),
                Constants.COMMAND_DATA_SEPARATOR, uniqueId.getText());

        if (encrypt) {
            String password = utility.generateRandomString(Constants.ENCRYPTION_KEY_LENGTH);
            data = utility.concatStrings(tempKeys.encryptToBase64(password), Constants.COMMAND_DATA_SEPARATOR,
                    utility.encrypt(data, password));
        }

        response = processTCPReturn(client.sendMessage(data, encrypt));

        if (response.getCode().equals(Constants.OK_RESULT)
                || response.getCode().equals(Constants.ALREADY_REGISTERED_RESULT)) {
            String[] responseData = response.getResponseSplit();
            String squareSafeName = safeString(responseData[3]);
            utility.deleteFile(utility.concatStrings(squareSafeName, Constants.PAUSE_FILE_EXT));
            utility.deleteFile(utility.concatStrings(squareSafeName, Constants.LEAVE_FILE_EXT));
            String temp = utility.concatStrings(Constants.MEMBER_COMMAND, Constants.COMMAND_DATA_SEPARATOR,
                    uniqueId.getText());
            String password = utility.generateRandomString(Constants.ENCRYPTION_KEY_LENGTH);
            data = utility.concatStrings(tempKeys.encryptToBase64(password), Constants.COMMAND_DATA_SEPARATOR,
                    utility.encrypt(temp, password));
            response = processTCPReturn(client.sendMessage(data, encrypt));
            utility.writeFile(utility.concatStrings(squareSafeName, Constants.MEMBERS_FILE_EXT),
                    response.getMessage().replace(Constants.COMMAND_DATA_SEPARATOR, Constants.NEWLINE));
            String info = utility.concatStrings(responseData[3], Constants.COMMA, client.getSquareId(), Constants.COMMA,
                    Constants.TAB_PREFIX, squareSafeName, Constants.COMMA, Constants.ZERO, Constants.NO_PASSWORD_VALUE);
            ISquare square = factory.createSquare(Constants.BASE_SQUARE, info, port.getText(),
                    remoteIP.getValue().getDisplay(),
                    factory.createSquareController(Constants.BASE_SQUARE_CONTROLLER, utility, this,
                            factory.createLogger(Constants.FILE_LOGGER,
                                    utility.concatStrings(uniqueId.getText(), Constants.LOG_FILE_EXT), utility),
                            factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility)),
                    utility, this, uniqueId.getText());
            utility.writeFile(utility.concatStrings(squareSafeName, Constants.SQUARE_FILE_EXT), info);
            setTabSquare(square);

            byte[] data1 = utility
                    .readBinaryFile(utility.concatStrings(Constants.MY_SQUARE_DEFAULT, Constants.ALIAS_FILE_EXT));
            utility.writeBinaryFile(utility.concatStrings(squareSafeName, Constants.ALIAS_FILE_EXT), data1);

            return true;
        }

        return false;
    }

    public void buildSquares() {
        String[] files = utility.getFiles(Constants.SQUARE_FILE_EXT);
        for (String file : files) {
            if (file.equals(utility.concatStrings(Constants.MY_SQUARE_DEFAULT, Constants.SQUARE_FILE_EXT))) {
                continue;
            }
            String contents = utility.readFile(file);
            setTabSquare(new Square(contents, port.getText(), remoteIP.getValue().getDisplay(),
                    factory.createSquareController(Constants.BASE_SQUARE_CONTROLLER, utility, this,
                            factory.createLogger(Constants.FILE_LOGGER,
                                    utility.concatStrings(uniqueId.getText(), Constants.LOG_FILE_EXT), utility),
                            factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility)),
                    utility, this, uniqueId.getText(), factory));
        }
    }

    private SquareResponse processTCPReturn(String result) {
        return new SquareResponse(result);
    }

    public void addPostMessages(ISquare square, VBox messageList, ScrollPane scrollPane, String message, long millis,
            String memberId) {
        String[] alreadyBlocked = utility.searchFile(
                utility.concatStrings(square.getSafeLowerName(), Constants.BLOCK_FILE_EXT), memberId,
                Constants.SEARCH_STARTS_WITH);
        if (alreadyBlocked.length > 0) {
            return;
        }
        if (knownPostMessages == null) {
            knownPostMessages = new ArrayList<>();
        }

        // protect against double messges
        if (knownPostMessages.contains(millis)) {
            return;
        }

        // know about the new message
        knownPostMessages.add(millis);

        if (message.contains(Constants.IMAGE_MARKER)) {
            buildImageMessage(message, messageList, millis);
        } else if (message.contains(Constants.VIDEO_MARKER)) {
            buildVideoMessage(message, messageList, millis);
        } else if (message.contains(Constants.FILE_MARKER)) {
            buildFileMessage(message, messageList, scrollPane, millis);
        } else if (message.contains(Constants.URL_MARKER)) {
            buildURLMessage(message, messageList, scrollPane, millis);
        } else {
            buildTextMessage(message, messageList, scrollPane);
        }

        // create a future task to scroll the message pane
        // give the UI time to redraw and re calcuate
        // otherwise it "scrolls" to the same place
        // thinking it is the bottom
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    Thread.sleep(210);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                scrollPane.setVvalue(Double.MAX_VALUE);
                return null;
            }
        };
        new Thread(task).start();
    }

    private void buildURLMessage(String message, VBox messageList, ScrollPane scrollPane, long millis) {
        HBox hbox = createHBox(0, 0, 0, 0);
        int index = utility.add(message.indexOf(utility.concatStrings(Constants.COLON, Constants.SPACE)),
                Constants.COLON.length(), Constants.SPACE.length());
        Label labelInfo = createLabel(message.substring(0, index), 0, 0, 0, 0);
        hbox.getChildren().add(labelInfo);
        Text label = new Text();
        String address = message.substring(index).replace(Constants.URL_MARKER, Constants.EMPTY_STRING);
        label.setText(address);
        label.setStyle(Constants.CSS_STYLE_HAND);
        label.setFill(Constants.LINK_COLOR);
        label.setUnderline(true);
        label.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                try {
                    URI uri = new URI(address);
                    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                        desktop.browse(uri);
                    }
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                } catch (URISyntaxException use) {
                    use.printStackTrace();
                }
            }
        });
        if (labelInfo.getText().contains(Constants.STAR)) {
            label.setStrikethrough(true);
        }
        setTextMaxWidth(label, scrollPane, getLabelWidth(labelInfo));
        hbox.getChildren().addAll(label);
        messageList.getChildren().add(hbox);

        if (postMessageWorkers == null) {
            postMessageWorkers = new ArrayList<>();
        }

        postMessageWorkers.add(new MessageWorker(label, scrollPane, labelInfo));
    }

    private void buildFileMessage(String message, VBox messageList, ScrollPane scrollPane, long millis) {
        HBox hbox = createHBox(0, 0, 0, 0);
        int index = utility.add(message.indexOf(utility.concatStrings(Constants.COLON, Constants.SPACE)),
                Constants.COLON.length(), Constants.SPACE.length());
        Label labelInfo = createLabel(message.substring(0, index), 0, 0, 0, 0);
        hbox.getChildren().add(labelInfo);
        Text label = new Text();
        String file = message.substring(index).replace(Constants.FILE_MARKER, Constants.EMPTY_STRING);
        label.setText(file);
        label.setStyle(Constants.CSS_STYLE_HAND);
        label.setFill(Constants.LINK_COLOR);
        label.setUnderline(true);
        label.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                try {
                    File f = new File(file);
                    Desktop.getDesktop().open(f);
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        });
        if (labelInfo.getText().contains(Constants.STAR)) {
            label.setStrikethrough(true);
        }
        setTextMaxWidth(label, scrollPane, getLabelWidth(labelInfo));
        hbox.getChildren().addAll(label);
        messageList.getChildren().add(hbox);

        if (postMessageWorkers == null) {
            postMessageWorkers = new ArrayList<>();
        }

        postMessageWorkers.add(new MessageWorker(label, scrollPane, labelInfo));
    }

    private void buildTextMessage(String message, VBox messageList, ScrollPane scrollPane) {
        HBox hbox = createHBox(0, 0, 0, 0);
        int index = utility.add(message.indexOf(utility.concatStrings(Constants.COLON, Constants.SPACE)),
                Constants.COLON.length(), Constants.SPACE.length());
        Label labelInfo = createLabel(message.substring(0, index), 0, 0, 0, 0);
        hbox.getChildren().add(labelInfo);
        Text label = new Text();
        label.setText(message.substring(index));
        if (labelInfo.getText().contains(Constants.STAR)) {
            label.setStrikethrough(true);
        }
        setTextMaxWidth(label, scrollPane, getLabelWidth(labelInfo));
        hbox.getChildren().addAll(label);
        messageList.getChildren().add(hbox);

        if (postMessageWorkers == null) {
            postMessageWorkers = new ArrayList<>();
        }

        postMessageWorkers.add(new MessageWorker(label, scrollPane, labelInfo));
    }

    private void buildImageMessage(String message, VBox messageList, long millis) {
        HBox hbox = createHBox(10, 0, 0, 0);
        int index = utility.add(message.indexOf(Constants.END_SQUARE_BRACKET), Constants.END_SQUARE_BRACKET.length());
        String file = message.substring(index, message.length());
        try (InputStream stream = new FileInputStream(file)) {
            Image image = new Image(stream);
            ImageView imageView = new ImageView();
            imageView.setUserData(millis);
            imageView.setFitHeight(Constants.IMAGE_SMALL_FIT_HEIGHT);
            imageView.setFitWidth(Constants.IMAGE_SMALL_FIT_WIDTH);
            imageView.setPreserveRatio(true);
            imageView.setImage(image);
            imageView.setStyle(Constants.CSS_STYLE_HAND);
            imageView.setOnMouseClicked(new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    MouseEvent me = (MouseEvent) event;
                    if (me == null) {
                        return;
                    }
                    int button = Constants.NO_BUTTON;
                    MouseButton mb = me.getButton();

                    if (mb.compareTo(MouseButton.PRIMARY) == Constants.EQUALS_VALUE) {
                        button = Constants.PRIMARY_BUTTON;
                    } else if (mb.compareTo(MouseButton.SECONDARY) == Constants.EQUALS_VALUE) {
                        button = Constants.SECONDARY_BUTTON;
                    }

                    processImageAction(button, file, millis);
                }
            });
            index = message.indexOf(utility.concatStrings(Constants.COLON, Constants.SPACE));
            Label label = createLabel(message.substring(0, index), 25, 0, 0, 0);
            hbox.getChildren().addAll(label, imageView);
            messageList.getChildren().addAll(hbox);
            if (images == null) {
                images = new ArrayList<>();
            }
            images.add(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processImageAction(int buttonClicked, String file, long millis) {
        if (buttonClicked == Constants.PRIMARY_BUTTON) {
            if (modalImageViewer == null) {
                modalImageViewer = factory.createModalViewer(Constants.BASE_MODAL_IMAGE_VIEWER);
            }
            modalImageViewer.show(file);
        } else if (buttonClicked == Constants.SECONDARY_BUTTON) {
            blockImage(file, millis);
        }
    }

    private void blockImage(String file, long millis) {
        IAlertBox alertBox = factory.createAlertBox(Constants.BASE_ALERT_BOX);
        IAlert alert = alertBox.createAlert("Block this image?", "Do you want to block this image?", "",
                AlertType.CONFIRMATION);
        ButtonType bt = alert.getSelectedButton();
        if (bt.equals(ButtonType.OK)) {
            byte[] data = utility.readBinaryFile(Constants.BLOCKED_IMAGE_FILE);
            utility.writeBinaryFile(file, data);
            for (ImageView image : images) {
                Long l = (Long) image.getUserData();
                if (l != null && l.equals(millis)) {
                    try (InputStream stream = new FileInputStream(file)) {
                        Image i = new Image(stream);
                        image.setImage(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }
        }
    }

    private void buildVideoMessage(String message, VBox messageList, long millis) {
        HBox hbox = createHBox(10, 0, 0, 0);
        int index = utility.add(message.indexOf(Constants.END_SQUARE_BRACKET), Constants.END_SQUARE_BRACKET.length());
        String file = message.substring(index, message.length());
        if (!utility.checkFileExists(file)) {
            return;
        }
        String f = new File(file).toURI().toString();
        Media media = new Media(f);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.0);
        mediaPlayer.setAutoPlay(true);
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setUserData(millis);
        mediaView.setFitHeight(Constants.IMAGE_SMALL_FIT_HEIGHT);
        mediaView.setFitWidth(Constants.IMAGE_SMALL_FIT_WIDTH);
        mediaView.setStyle(Constants.CSS_STYLE_HAND);
        mediaView.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                MouseEvent me = (MouseEvent) event;
                if (me == null) {
                    return;
                }
                int button = Constants.NO_BUTTON;
                MouseButton mb = me.getButton();

                if (mb.compareTo(MouseButton.PRIMARY) == Constants.EQUALS_VALUE) {
                    button = Constants.PRIMARY_BUTTON;
                } else if (mb.compareTo(MouseButton.SECONDARY) == Constants.EQUALS_VALUE) {
                    button = Constants.SECONDARY_BUTTON;
                }

                processVideoAction(file, button, millis);
            }
        });
        if (videos == null) {
            videos = new ArrayList<>();
        }
        videos.add(mediaView);
        index = message.indexOf(utility.concatStrings(Constants.COLON, Constants.SPACE));
        Label label = createLabel(message.substring(0, index), 25, 0, 0, 0);
        hbox.getChildren().addAll(label, mediaView);
        messageList.getChildren().addAll(hbox);
    }

    private void processVideoAction(String file, int buttonClicked, long millis) {
        if (buttonClicked == Constants.PRIMARY_BUTTON) {
            if (modalVideoViewer == null) {
                modalVideoViewer = factory.createModalViewer(Constants.BASE_MODAL_VIDEO_VIEWER);
            }
            modalVideoViewer.show(file);
        } else if (buttonClicked == Constants.SECONDARY_BUTTON) {
            IAlertBox alertBox = factory.createAlertBox(Constants.BASE_ALERT_BOX);
            IAlert alert = alertBox.createAlert("Block this video?", "Do you want to block this video?", "",
                    AlertType.CONFIRMATION);
            ButtonType bt = alert.getSelectedButton();
            if (bt.equals(ButtonType.OK)) {
                byte[] data = utility.readBinaryFile(Constants.BLOCKED_VIDEO_FILE);
                utility.writeBinaryFile(file, data);
                for (MediaView video : videos) {
                    Long l = (Long) video.getUserData();
                    if (l != null && l.equals(millis)) {
                        String f = new File(file).toURI().toString();
                        Media media = new Media(f);
                        MediaPlayer mediaPlayer = new MediaPlayer(media);
                        mediaPlayer.setVolume(0.0);
                        mediaPlayer.setAutoPlay(true);
                        video.setMediaPlayer(mediaPlayer);
                        return;
                    }
                }
            }
        }
    }

    private void setTextMaxWidth(Text text, ScrollPane scrollPane, double other) {
        text.setWrappingWidth(scrollPane.getWidth() - 25 - other);
    }

    private double getLabelWidth(Label theLabel) {
        Text theText = new Text(theLabel.getText());
        theText.setFont(theLabel.getFont());
        return theText.getBoundsInLocal().getWidth();
    }

    private void processCreateSquare(String input) {
        String memberInfo = utility.concatStrings(defaultName.getText(), Constants.FILE_DATA_SEPARATOR, publicKey,
                Constants.FILE_DATA_SEPARATOR, remoteIP.getValue().getDisplay(), Constants.FILE_DATA_SEPARATOR,
                port.getText(), Constants.FILE_DATA_SEPARATOR, uniqueId.getText());
        utility.writeFile(utility.concatStrings(input.replace(Constants.SPACE, Constants.UNDERSCORE).toLowerCase(),
                Constants.MEMBERS_FILE_EXT), memberInfo);
        // My Square,a7075b5b-b91d-4448-a0f9-d9b0bec1a726,tabDefaultSquare,0,~~~~~~~
        String uuid = utility.createUUID();
        String safeName = input.replace(Constants.SPACE, Constants.UNDERSCORE).toLowerCase();
        String contents = utility.concatStrings(input, Constants.COMMA, uuid, Constants.COMMA,
                Constants.TAB_PREFIX, safeName, Constants.COMMA, Constants.ZERO, Constants.NO_PASSWORD_VALUE);
        utility.writeFile(utility.concatStrings(safeName, Constants.SQUARE_FILE_EXT), contents);
        setTabSquare(new Square(contents, port.getText(), remoteIP.getValue().getDisplay(),
                factory.createSquareController(Constants.BASE_SQUARE_CONTROLLER, utility, this,
                        factory.createLogger(Constants.FILE_LOGGER,
                                utility.concatStrings(uniqueId.getText(), Constants.LOG_FILE_EXT), utility),
                        factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility)),
                utility, this, uniqueId.getText(), factory));
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

    public void updateDefaultNameInMemberFiles(String name) {
        String[] files = utility.getFiles(Constants.MEMBERS_FILE_EXT);

        for (String file : files) {
            String memberInfo = utility.readFile(file);
            String[] lines = memberInfo.split(Constants.READ_FILE_DATA_SEPARATOR);
            int i = 0;
            for (String line : lines) {
                String[] lineData = line.split(Constants.FILE_DATA_SEPARATOR, 2);
                String ipData = lineData[1];
                if (ipData.contains(uniqueId.getText())) {
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

    private void updatePortInMemberFiles(String port, String uniqueId) {
        String[] files = utility.getFiles(Constants.MEMBERS_FILE_EXT);

        for (String file : files) {
            String memberInfo = utility.readFile(file);
            String[] lines = memberInfo.split(Constants.READ_FILE_DATA_SEPARATOR);
            int i = 0;
            for (String line : lines) {
                if (line.contains(uniqueId)) {
                    String[] lineData = line.split(Constants.FILE_DATA_SEPARATOR);
                    lines[i] = utility.concatStrings(lineData[0], Constants.FILE_DATA_SEPARATOR, lineData[1],
                            Constants.FILE_DATA_SEPARATOR, lineData[2], Constants.FILE_DATA_SEPARATOR, port,
                            Constants.FILE_DATA_SEPARATOR, lineData[4]);
                    break;
                }
                i++;
            }
            String newMemberInfo = String.join(Constants.NEWLINE, lines);
            utility.deleteFile(file);
            utility.writeFile(file, newMemberInfo);
        }
    }

    private void updateIPAddressInMemberFiles(String ip, String uniqueId) {
        String[] files = utility.getFiles(Constants.MEMBERS_FILE_EXT);

        for (String file : files) {
            String memberInfo = utility.readFile(file);
            String[] lines = memberInfo.split(Constants.READ_FILE_DATA_SEPARATOR);
            int i = 0;
            for (String line : lines) {
                if (line.contains(uniqueId)) {
                    String[] lineData = line.split(Constants.FILE_DATA_SEPARATOR);
                    lines[i] = utility.concatStrings(lineData[0], Constants.FILE_DATA_SEPARATOR, lineData[1],
                            Constants.FILE_DATA_SEPARATOR, ip, Constants.FILE_DATA_SEPARATOR, lineData[3],
                            Constants.FILE_DATA_SEPARATOR, lineData[4]);
                    break;
                }
                i++;
            }
            String newMemberInfo = String.join(Constants.NEWLINE, lines);
            utility.deleteFile(file);
            utility.writeFile(file, newMemberInfo);
        }
    }

    private void updateAliases(String aliasList) {
        String[] aliases = aliasList.trim().toLowerCase().split(Constants.SEMI_COLON);
        for (ISquare square : squares) {
            processAlias(square, aliases);
        }
    }

    private void processAlias(ISquare square, String[] aliases) {
        RequesterInfo requesterInfo = new RequesterInfo(utility.readFile(Constants.IP_FILE));
        for (String a : aliases) {
            if (a.equals(Constants.EMPTY_STRING)) {
                continue;
            }
            String request = utility.concatStrings(Constants.UNENCRYPTED_FLAG, Constants.COMMAND_DATA_SEPARATOR,
                    square.getInvite(), Constants.COMMAND_DATA_SEPARATOR, Constants.REGISTER_ALIAS_COMMAND,
                    Constants.COMMAND_DATA_SEPARATOR, Constants.NULL_TEXT, Constants.FILE_DATA_SEPARATOR, a,
                    Constants.FILE_DATA_SEPARATOR, port.getText(), Constants.FILE_DATA_SEPARATOR, uniqueId.getText());
            square.getController().processRequest(request, requesterInfo);
        }
    }
}

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SampleController {
    private static final String TILDE = "~";
    private static final String INVITATION_LABEL_TEXT = "Invitation:";
    private static final String INVITE_CODE_LABEL = "Invite Code:";
    private static final int TEXTFIELD_WIDTH = 400;
    private static final String PASSWORD_PROMPT_TEXT = "Password";
    private static final String EMPTY_STRING = "";
    private static final String UPDATE_BUTTON_TEXT = "Update";
    private static final String PRIVATE_LABEL_TEXT = "Is Private:";
    private static final String DATA_SEPARATOR = "%%%";
    private static final String ENCRYPTION_FLAG = "e";
    private static final String JOIN_COMMAND = "join";
    private static final String MEMBER_COMMAND = "members";
    private static final String REQUEST_PUBLIC_KEY_COMMAND = "pkey";
    private static final String OK_RESULT = "200";
    private static final String ALREADY_REGISTERED_RESULT = "460";

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
    private TextField remoteIP;

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
        TextDialogBox dialogBox = new TextDialogBox("Join Square", "Paste the Invitation", EMPTY_STRING, this);
        dialogBox.show();
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

    public void setRemoteIP(String ip) {
        if (remoteIP != null) {
            remoteIP.setText(ip);
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

        // privacy setting
        HBox privacyControls = createPrivacyControls(square);
        main.getChildren().add(privacyControls);

        // invite controls
        HBox generateInviteControls = createGenerateInviteControls(square, index);
        main.getChildren().add(generateInviteControls);

        // add main
        tab.setContent(main);
        tabPane.getTabs().add(0, tab);
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

    private HBox createPrivacyControls(Square square) {
        HBox privacyControls = createHBox(0, 10, 10, 10);

        Label privateLabel = createLabel(PRIVATE_LABEL_TEXT, 3, 5, 0, 12);

        TextField password = createTextField(square.getPassword(), PASSWORD_PROMPT_TEXT, true, 0);

        CheckBox privateValueCheckBox = createPrivateValueCheckBox(square, password);

        Label spacer = createLabel(EMPTY_STRING, 0, 5, 0, 5);

        Button updatePrivacy = createUpdatePrivacyButton(privateValueCheckBox, password);

        privacyControls.getChildren().addAll(privateLabel, privateValueCheckBox, password, spacer, updatePrivacy);

        return privacyControls;
    }

    private CheckBox createPrivateValueCheckBox(Square square, TextField password) {
        CheckBox privateValueCheckBox = new CheckBox();
        privateValueCheckBox.setPadding(new Insets(3, 5, 0, 5));
        privateValueCheckBox.setSelected(square.isPrivate());
        privateValueCheckBox.setId(square.getSafeName());
        privateValueCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldVal, Boolean newVal) {
                Square square = getSquare(unsafeString(privateValueCheckBox.getId()));
                if (square != null) {
                    square.setPrivate(newVal);
                    if (!newVal) {
                        square.setPassword(EMPTY_STRING);
                        password.setText(EMPTY_STRING);
                    }
                    int index = squareNames.indexOf(square.getName());
                    squares.set(index, square);
                }
            }
        });

        return privateValueCheckBox;
    }

    private Button createUpdatePrivacyButton(CheckBox privateValueCheckBox, TextField password) {
        Button updatePrivacy = new Button(UPDATE_BUTTON_TEXT);
        updatePrivacy.setOnAction(event -> {
            Square newSquare = getSquare(unsafeString(privateValueCheckBox.getId()));
            if (newSquare != null) {
                TextField invite = (TextField) newSquare.getTemp();
                if (invite != null) {
                    if (newSquare.isPrivate() && password.getText().trim().equals(EMPTY_STRING)) {
                        AlertBox.createAlert("Invalid Password", "Password is required.",
                                "Add a password to private Square.");
                    } else {
                        newSquare.setPassword(password.getText().trim());
                        invite.setText(buildInviteCode(newSquare, determineSquarePrivacy(newSquare)));
                        int newIndex = squareNames.indexOf(newSquare.getName());
                        squares.set(newIndex, newSquare);
                        parent.updateSquare(newSquare);
                    }
                }
            }
        });

        return updatePrivacy;
    }

    private String buildInviteCode(Square square, String encrypt) {
        return encrypt + TILDE + square.getIP() + TILDE + square.getPort() + TILDE + square.getInvite();
    }

    private String determineSquarePrivacy(Square square) {
        String encrypt = "e";
        if (!square.isPrivate()) {
            encrypt = "u";
        }

        return encrypt;
    }

    private Square getSquare(String name) {
        if (squareNames.contains(name)) {
            int index = squareNames.indexOf(name);
            return squares.get(index);
        }

        return null;
    }

    public Square getSquareByInvite(String id) {
        if (squareInvites.contains(id)) {
            int index = squareInvites.indexOf(id);
            return squares.get(index);
        }

        return null;
    }

    private String unsafeString(String s) {
        return s.replace('_', ' ');
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
        String data = JOIN_COMMAND + DATA_SEPARATOR + defaultName.getText() + DATA_SEPARATOR + publicKey + DATA_SEPARATOR + remoteIP.getText() + DATA_SEPARATOR
                        + port.getText() + DATA_SEPARATOR + uniqueId.getText();
        
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
            String temp = MEMBER_COMMAND + DATA_SEPARATOR + uniqueId.getText();
            Utility utility = Utility.create();
            String password = utility.generateRandomString(16);
            data = tempKeys.encryptToBase64(password) + DATA_SEPARATOR + utility.encrypt(temp, password);
            response = processTCPReturn(client.sendMessage(data, encrypt));
            utility.writeFile("test.txt", response.getMessage().replace(DATA_SEPARATOR, "\n"));
         }
    }

    private SquareResponse processTCPReturn(String result) {
        return new SquareResponse(result);
    }
}

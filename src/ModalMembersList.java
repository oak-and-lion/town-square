import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ModalMembersList extends BaseViewer implements IModalViewer {
    private IUtility utility;
    private ISquare square;

    public ModalMembersList(IUtility utility, ISquare square, Stage parentStage) {
        super(parentStage);
        this.utility = utility;
        this.square = square;
    }

    public void show(String file) {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(5, 5, 5, 5));
        vbox.setMinWidth(500);
        vbox.setMinHeight(400);
        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setMinHeight(395);
        scrollPane.setMinWidth(495);
        String[] membersSplit = utility.readFile(file).split(Constants.READ_FILE_DATA_SEPARATOR);
        StringBuilder members = new StringBuilder();
        boolean first = true;
        ICommandController controller = square.getSampleController().getCommandController();

        MemberInfoList membersInfoList = new MemberInfoList();
        for (String member : membersSplit) {
            membersInfoList.add(new MemberInfo(member, utility));
        }

        String current = Constants.EMPTY_STRING;
        String spacing;
        for (MemberInfo member : membersInfoList.getAll()) {
            if (!first) {
                members.append(Constants.NEWLINE);
                if (!current.equals(member.getPublicKey())) {
                    members.append("-------------------");
                    members.append(Constants.NEWLINE);
                }
            } else {
                first = false;
            }

            if (!current.equals(member.getPublicKey())) {
                current = member.getPublicKey();
                spacing = Constants.EMPTY_STRING;
            } else {
                spacing = Constants.FOUR_SPACES;
            }

            String ack;
            String myIp = utility.readFile(Constants.IP_FILE);
            String myPort = utility.readFile(Constants.PORT_FILE);
            if (member.getIp().equals(myIp) && member.getPort().equals(myPort)) {
                spacing = utility.concatStrings(Constants.THREE_STRINGS, Constants.HASHTAG);
            } else {
                ack = utility.concatStrings(Constants.FORWARD_SLASH, Constants.ACK_COMMAND, Constants.SPACE,
                        member.getIp(), Constants.FILE_DATA_SEPARATOR, member.getPort());

                BooleanString[] ackResult = controller.processCommand(ack, square);
                if (ackResult[0].getBoolean()) {
                    members.append(Constants.STAR);
                    members.append(Constants.STAR);
                }
            }
            members.append(spacing);
            members.append(member.getName());
            members.append(Constants.SPACE);
            members.append(Constants.DASH);
            members.append(Constants.SPACE);
            members.append(member.getIp());
            members.append(Constants.COLON);
            members.append(member.getPort());
        }
        Text label = new Text(members.toString());

        vbox.getChildren().add(label);
        // setting group and scene
        Group root = new Group();
        root.getChildren().add(scrollPane);
        showStage(root, utility.concatStrings(square.getName(), " Members"));
    }
}

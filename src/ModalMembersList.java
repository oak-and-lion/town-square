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

        for (String member : membersSplit) {
            if (!first) {
                members.append(Constants.NEWLINE);
                members.append("-------------------");
                members.append(Constants.NEWLINE);
            } else {
                first = false;
            }
            String[] m = member.split(Constants.FILE_DATA_SEPARATOR);

            String ack = Constants.ACK_COMMAND;
            String myIp = utility.readFile(Constants.IP_FILE);
            String myPort = utility.readFile(Constants.PORT_FILE);
            if (m[2].equals(myIp) && m[3].equals(myPort)) {
                // do nothing
            } else {
                ack = utility.concatStrings(Constants.FORWARD_SLASH, Constants.ACK_COMMAND, Constants.SPACE, m[2],
                        Constants.FILE_DATA_SEPARATOR, m[3]);

                BooleanString[] ackResult = controller.processCommand(ack, square);
                if (ackResult[0].getBoolean()) {
                    members.append(Constants.STAR);
                    members.append(Constants.STAR);
                }
            }
            members.append(m[0]);
            members.append(Constants.SPACE);
            members.append(Constants.DASH);
            members.append(Constants.SPACE);
            members.append(m[2]);
            members.append(Constants.COLON);
            members.append(m[3]);
        }
        Text label = new Text(members.toString());

        // label.setPadding(new Insets(5, 5, 5, 5));
        vbox.getChildren().add(label);
        // setting group and scene
        Group root = new Group();
        root.getChildren().add(scrollPane);
        showStage(root, utility.concatStrings(square.getName(), " Members"));
    }
}

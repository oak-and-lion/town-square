import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public interface IDialogController {
    ISquare getSquareByInvite(String id);
    String getDefaultName();
    void setDefaultName(String name);
    void setVersion(String version);
    void setAlias(String alias);
    void setParent(IApp app);
    void addPostMessages(ISquare square, VBox vbox, ScrollPane scrollPane, String message, long millis, String memberId);
    void setUtilityController(IUtility utility);
    void setUniqueId(String uniqueId);
    void setPort(String port);
    void buildSquares();
    void setTabSquare(ISquare square);
    void setPublicKey(String publicKey);
    void setRemoteIP(ObservableList<IPAddress> ips, String s);
    void processPendingInvites();
    void resizeControls(double width, double height);
    void setStage(Stage stage);
    void updateDefaultNameInMemberFiles(String name);
    void setCommandController(ICommandController value);
    void showAbout();
    void showList(String[] items, String listTitle, String listHeader);
    void updatePauseNotification(ISquare square, boolean paused);
    void processImageAction(int buttonClicked, String file, long millis);
    void setFactory(IFactory factory);
    IApp getParent();
}

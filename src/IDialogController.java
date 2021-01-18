import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public interface IDialogController {
    ISquare getSquareByInvite(String id);
    String getDefaultName();
    void setDefaultName(String name);
    void setVersion(String version);
    void setParent(IApp app);
    void addPostMessages(VBox vbox, ScrollPane scrollPane, String message);
    void setUtilityController(IUtility utility);
    void setUniqueId(String uniqueId);
    void setPort(String port);
    void buildSquares();
    void setTabSquare(ISquare square);
    void setPublicKey(String publicKey);
    void setRemoteIP(ObservableList<IPAddress> ips, String s);
    void processPendingInvites();
}

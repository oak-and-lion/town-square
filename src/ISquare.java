import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public interface ISquare {
    String getIP();
    void setIP(String ip);
    String getPort();
    String getInvite();
    Object getTemp();
    String getName();
    String getId();
    void setPostsScrollPane(ScrollPane scrollPane);
    void setPostsVBox(VBox vbox);
    void setTemp(Object object);
    String getSafeLowerName();
}

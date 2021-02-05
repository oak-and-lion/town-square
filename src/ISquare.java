import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
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
    VBox getPostsVBox();
    ScrollPane getPostsScrollPane();
    IDialogController getSampleController();
    void setLastKnownPost(int lastKnownPost);
    int getLastKnownPost();
    void addPostMessage(PostMessage message);
    void setTab(Tab tab);
    Tab getTab();
    void runClientFunctions(int maxRuns);
    ISquareController getController();
}

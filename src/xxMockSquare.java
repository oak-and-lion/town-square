import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class xxMockSquare implements ISquare {
    public String getIP(){
        return Constants.EMPTY_STRING;
    }
    public void setIP(String ip){
        // not needed
    }
    public String getPort() {
        return Constants.EMPTY_STRING;
    }
    public String getInvite(){
        return Constants.EMPTY_STRING;
    }
    public Object getTemp(){
        return null;
    }
    public String getName(){
        return Constants.EMPTY_STRING;
    }
    public String getId(){
        return Constants.EMPTY_STRING;
    }
    public void setPostsScrollPane(ScrollPane scrollPane){
        // not needed
    }
    public void setPostsVBox(VBox vbox){
        // not needed
    }
    public void setTemp(Object object){
        // not needed
    }
    public String getSafeLowerName() {
        return "my_square";
    }
    public VBox getPostsVBox() {
        return null;
    }
    public ScrollPane getPostsScrollPane() {
        return null;
    }
    public IDialogController getSampleController(){
        return null;
    }
    public int getLastKnownPost() {
        return -1;
    }
    public void setLastKnownPost(int lastKnownPost) {
        // not needed
    }
}

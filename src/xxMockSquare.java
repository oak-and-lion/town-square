import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

public class xxMockSquare implements ISquare {
    private IDialogController controller;
    String port;
    public xxMockSquare() {
        this.port = "44123";
    }

    public xxMockSquare(IDialogController controller) {
        this.controller = controller;
        this.port = "44123";
    }
    public String getIP(){
        return Constants.EMPTY_STRING;
    }
    public void setIP(String ip){
        // not needed
    }
    public String getPort() {
        return port;
    }
    public void setPort(String value) {
        port = value;
    }
    public String getInvite(){
        return "invite123";
    }
    public Object getTemp(){
        return null;
    }
    public String getName(){
        return getTrueName();
    }
    public String getTrueName() {
        return Constants.DEFAULT_SQUARE_NAME;
    }
    public String getId(){
        return "123";
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
        return getName().toLowerCase().replace(" ", "_");
    }
    public VBox getPostsVBox() {
        return null;
    }
    public ScrollPane getPostsScrollPane() {
        return null;
    }
    public IDialogController getSampleController(){
        return this.controller;
    }
    public int getLastKnownPost() {
        return -1;
    }
    public void setLastKnownPost(int lastKnownPost) {
        // not needed
    }
    public void addPostMessage(PostMessage message) {
        // not needed
    }
    public void setTab(Tab tab){
        // not needed
    }
    public Tab getTab() {
        return null;
    }
    public void runClientFunctions(int maxRuns) {
        // not needed
    }
    public ISquareController getController() {
        return null;
    }
}

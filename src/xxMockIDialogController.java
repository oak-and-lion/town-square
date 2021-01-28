import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class xxMockIDialogController implements IDialogController {
    public ISquare getSquareByInvite(String id){
        return new xxMockSquare();
    }
    public String getDefaultName() {
        return Constants.EMPTY_STRING;
    }
    public void setDefaultName(String name){
        // not needed 
    }
    public void setVersion(String version){
        // not needed 
    }
    public void setAlias(String alias){
        // not needed 
    }
    public void setParent(IApp app){
        // not needed 
    }
    public void addPostMessages(VBox vbox, ScrollPane scrollPane, String message, long millis){
        // not needed 
    }
    public void setUtilityController(IUtility utility){
        // not needed 
    }
    public void setUniqueId(String uniqueId){
        // not needed 
    }
    public void setPort(String port){
        // not needed 
    }
    public void buildSquares(){
        // not needed 
    }
    public void setTabSquare(ISquare square){
        // not needed 
    }
    public void setPublicKey(String publicKey){
        // not needed 
    }
    public void setRemoteIP(ObservableList<IPAddress> ips, String s){
        // not needed 
    }
    public void processPendingInvites(){
        // not needed 
    }

    public void setStage(Stage stage) {
        // not neededed
    }

    public void resizeControls(double width, double height) {
        // not needed
    }

    @Override
    public void updateDefaultNameInMemberFiles(String name) {
        // not needed
    }

    public void setCommandController(ICommandController value) {
        // not needed
    }
}

import javafx.scene.control.Alert.AlertType;

public class xxMockIAlertBox implements IAlertBox {

    public IAlert createAlert(String title, String headerString, String content, AlertType type) {
        IUtility utility = Utility.create();
        utility.writeFile("alert.txt", "alert created");
        return new xxMockIAlert();
    }    
}

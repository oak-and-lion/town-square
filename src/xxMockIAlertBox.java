import javafx.scene.control.Alert.AlertType;

public class xxMockIAlertBox implements IAlertBox {

    public IAlert createAlert(String title, String headerString, String content, AlertType type) {
        IUtility utility = Utility.create(new xxMockIDialogController(), new xxMockFactory());
        utility.writeFile("alert.txt", "alert created");
        return new xxMockIAlert();
    } 

    public IAlert createAlert(String title, String headerText, String content, AlertType type, double width, double height) {
        IUtility utility = Utility.create(new xxMockIDialogController(), new xxMockFactory());
        utility.writeFile("alert.txt", "alert created");
        return new xxMockIAlert();
    }
}

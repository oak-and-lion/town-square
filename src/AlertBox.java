import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertBox {
    private AlertBox() {}
    
    public static Alert createAlert(String title, String headerText, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(content);
        alert.showAndWait().ifPresent(rs -> {
        });

        return alert;
    }
}

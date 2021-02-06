import javafx.scene.control.Alert.AlertType;

public interface IAlertBox {
    IAlert createAlert(String title, String headerText, String content, AlertType type);
    IAlert createAlert(String title, String headerText, String content, AlertType type, double width, double height);
}

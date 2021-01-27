import javafx.scene.control.Alert.AlertType;

public class AlertBox implements IAlertBox {
    private static IAlertBox ialertBox;

    private AlertBox() {
    }

    public static IAlertBox create() {
        if (ialertBox == null) {
            ialertBox = new AlertBox();
        }

        return ialertBox;
    }

    public IAlert createAlert(String title, String headerText, String content, AlertType type) {
        IAlert alert = new TownSquareAlert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(content);
        alert.showAndWait().ifPresent(alert::setSelectedButton);

        return alert;
    }
}

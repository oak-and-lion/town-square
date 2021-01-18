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

    public IAlert createAlert(String title, String headerText, String content) {
        IAlert alert = new TownSquareAlert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(content);
        alert.showAndWait().ifPresent(rs -> {
        });

        return alert;
    }
}

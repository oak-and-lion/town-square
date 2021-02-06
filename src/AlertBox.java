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
        return createAlert(title, headerText, content, type, Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT);
    }

    public IAlert createAlert(String title, String headerText, String content, AlertType type, double width, double height) {
        IAlert alert = new TownSquareAlert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(content);
        if (width != Constants.DEFAULT_WIDTH) {
            alert.setWidth(width);
        }
        if (height != Constants.DEFAULT_HEIGHT) {
            alert.setHeight(height);
        }
        alert.showAndWait().ifPresent(alert::setSelectedButton);

        return alert;
    }
}

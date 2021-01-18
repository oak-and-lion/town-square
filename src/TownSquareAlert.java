import javafx.scene.control.Alert;

public class TownSquareAlert extends Alert implements IAlert {
    public TownSquareAlert(AlertType type) {
        super(type);
    }
}

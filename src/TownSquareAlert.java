import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class TownSquareAlert extends Alert implements IAlert {
    ButtonType selectedButton;

    public TownSquareAlert(AlertType type) {
        super(type);
    }

    public ButtonType getSelectedButton() {
        return selectedButton;
    }

    public void setSelectedButton(ButtonType type) {
        selectedButton = type;
    }
}

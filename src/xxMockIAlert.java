import java.util.Optional;

import javafx.scene.control.ButtonType;

public class xxMockIAlert implements IAlert {
    public void setHeaderText(String s) {
        // empty mock
    }
    public void setContentText(String s) {
        // empty mock
    }
    public void setTitle(String s) {
        // empty mock
    }
    public Optional<ButtonType> showAndWait() {
        return Optional.ofNullable(null);
    }

    public void setSelectedButton(ButtonType type) {
        // not needed
    }

    public ButtonType getSelectedButton() {
        return ButtonType.OK;
    }
}

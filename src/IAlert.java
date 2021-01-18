import java.util.Optional;

import javafx.scene.control.ButtonType;

public interface IAlert {
    void setTitle(String title);
    void setHeaderText(String header);
    void setContentText(String content);
    Optional<ButtonType> showAndWait();
}

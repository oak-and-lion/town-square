import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BaseViewer {
    private Stage primaryStage;
    private Stage parentStage;

    public BaseViewer(Stage parentStage) {
        primaryStage = new Stage();
        this.parentStage = parentStage;
    }

    void close() {
        // override in inherited class
    }

    void showStage(Group root, String title) {
        Scene scene = new Scene(root, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        primaryStage.initOwner(this.parentStage);
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.setOnCloseRequest(e -> close());
        primaryStage.showAndWait();
    }
}

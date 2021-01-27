import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BaseViewer {
    private Stage primaryStage;

    public BaseViewer() {
        primaryStage = new Stage();
    }

    void close() {
        // override in inherited class
    }

    void showStage(Group root) {
        Scene scene = new Scene(root, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Video");
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> close());
    }
}

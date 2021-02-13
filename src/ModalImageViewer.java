import java.io.FileInputStream;
import java.io.InputStream;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ModalImageViewer extends BaseViewer implements IModalViewer {
    public ModalImageViewer(Stage parentStage) {
        super(parentStage);
    }

    public void show(String imageFile) {
        try (InputStream stream = new FileInputStream(imageFile)) {
            Image image = new Image(stream);
            ImageView imageView = new ImageView();
            imageView.setFitHeight(500);
            imageView.setFitWidth(400);
            imageView.setPreserveRatio(true);
            imageView.setImage(image);
        
            //setting group and scene   
            Group root = new Group();  
            root.getChildren().add(imageView);  
            showStage(root, "Image");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

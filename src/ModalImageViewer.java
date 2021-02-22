import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ModalImageViewer extends BaseViewer implements IModalViewer {
    private IUtility utility;
    public ModalImageViewer(Stage parentStage, IUtility utility) {
        super(parentStage);
        this.utility = utility;
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
            utility.logError(utility.concatStrings(e.getMessage(), Constants.NEWLINE, Arrays.toString(e.getStackTrace())));
        }
    }
}

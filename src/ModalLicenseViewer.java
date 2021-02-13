import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ModalLicenseViewer extends BaseViewer implements IModalViewer {
    public ModalLicenseViewer(Stage parentStage) {
        super(parentStage);
    }
    
    public void show(String license) {
            Label image = new Label(license);
            image.setPadding(new Insets(10,10,10,10));
            //setting group and scene   
            Group root = new Group();  
            root.getChildren().add(image);  
            showStage(root, "Usage License");
    }
}

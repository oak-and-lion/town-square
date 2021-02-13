import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class ModalVideoViewer extends BaseViewer implements IModalViewer {
    
    private MediaPlayer mediaPlayer;
    public ModalVideoViewer(Stage parentStage) {
        super(parentStage);
    }

    public void show(String file) {
        try (InputStream stream = new FileInputStream(file)) {
            VBox vbox = new VBox();
            vbox.setLayoutX(50);
            vbox.setLayoutY(30);
            String f = new File(file).toURI().toString();
            Media media = new Media(f);
            mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);
            mediaView.setFitHeight(Constants.IMAGE_LARGE_FIT_HEIGHT);
            mediaView.setFitWidth(Constants.IMAGE_LARGE_FIT_WIDTH);
            mediaPlayer.setAutoPlay(true);
            mediaPlayer.setVolume(0.1);

            VBox vbox2 = new VBox();
            Button button = new Button("Pause");
            button.setPadding(new Insets(0, 0, 0, 0));
            vbox2.setLayoutX(410);
            vbox2.getChildren().add(button);
            //setting group and scene   
            Group root = new Group();  
            vbox.getChildren().addAll(mediaView, vbox2);
            root.getChildren().addAll(vbox);
            showStage(root, "Video");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    void close() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javafx.scene.Group;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class ModalVideoViewer extends BaseViewer implements IModalViewer {
    
    private MediaPlayer mediaPlayer;
    public ModalVideoViewer() {
        super();
    }

    public void show(String file) {
        try (InputStream stream = new FileInputStream(file)) {
            String f = new File(file).toURI().toString();
            Media media = new Media(f);
            mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);
            mediaView.setFitHeight(Constants.IMAGE_LARGE_FIT_HEIGHT);
            mediaView.setFitWidth(Constants.IMAGE_LARGE_FIT_WIDTH);
            mediaPlayer.setAutoPlay(true);
            mediaPlayer.setVolume(0.1);
            //setting group and scene   
            Group root = new Group();  
            root.getChildren().add(mediaView);  
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
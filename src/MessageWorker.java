import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;

public class MessageWorker {
    private Label messageLabel;
    private ScrollPane parentPane;
    private Label leftLabel;

    public MessageWorker(Label label, ScrollPane scrollPane, Label leftLabel) {
        this.messageLabel = label;
        this.parentPane = scrollPane;
        this.leftLabel = leftLabel;
    }

    public void recalculateWidth() {
        setLabelMaxWidth(messageLabel, parentPane, getLabelWidth(leftLabel));
    }

    private void setLabelMaxWidth(Label label, ScrollPane scrollPane, double other) {
        label.setMaxWidth(scrollPane.getWidth() - 50 - other);
        label.setMinWidth(scrollPane.getWidth() - 50 - other);
    }

    private double getLabelWidth(Label theLabel) {
        if (theLabel == null) {
            return 0;
        }
        Text theText = new Text(theLabel.getText());
        theText.setFont(theLabel.getFont());
        return theText.getBoundsInLocal().getWidth();
    }
}

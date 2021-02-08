import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class TownSquareButton extends Button implements ITownSquareButton {
    private ISquare square;
    private TextField postMessage;

    public TownSquareButton(String buttonText, ISquare s, TextField textField) {
        super(buttonText);
        square = s;
        postMessage = textField;
    }

    public ISquare getSquare() {
        return square;
    }

    public String getPostMessage() {
        if (postMessage == null) {
            return Constants.EMPTY_STRING;
        }
        return postMessage.getText();
    }

    public void clearPostMessage() {
        if (postMessage != null) {
            postMessage.setText(Constants.EMPTY_STRING);
        }
    }
}

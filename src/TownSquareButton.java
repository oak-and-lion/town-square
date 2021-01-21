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
        return postMessage.getText();
    }

    public void clearPostMessage() {
        postMessage.setText(Constants.EMPTY_STRING);
    }
}

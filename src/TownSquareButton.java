import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class TownSquareButton extends Button {
    private ISquare square;
    private TextField postMessage;

    public TownSquareButton(String text, ISquare s, TextField textField) {
        super(text);
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
        postMessage.setText("");
    }
}

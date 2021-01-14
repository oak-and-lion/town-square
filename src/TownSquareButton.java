import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class TownSquareButton extends Button {
    private Square square;
    private TextField postMessage;

    public TownSquareButton(String text, Square s, TextField textField) {
        super(text);
        square = s;
        postMessage = textField;
    }

    public Square getSquare() {
        return square;
    }

    public String getPostMessage() {
        return postMessage.getText();
    }

    public void clearPostMessage() {
        postMessage.setText("");
    }
}

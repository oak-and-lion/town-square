import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public interface ITownSquareButton {
    ISquare getSquare();
    String getPostMessage();
    void clearPostMessage();
    void fire();
    void setOnAction(EventHandler<ActionEvent> event);    
}

import java.util.Optional;

import javafx.scene.control.TextInputDialog;

public class TextDialogBox implements ITextDialogBox {
    private String title;
    private String headerText;
    private String content;
    private ITextDialogBoxCallback controller;
    private double width;
    private int type;

    public TextDialogBox(String title, String headerText, String content, ITextDialogBoxCallback controller, double width, int type) {
        this.title = title;
        this.headerText = headerText;
        this.content = content;
        this.controller = controller;
        this.width = width;
        this.type = type;
    }

    public void show() {
        var txtDlg = new TextInputDialog();
        txtDlg.getDialogPane().setMinWidth(width);
        txtDlg.setTitle(title);
        txtDlg.setHeaderText(headerText);
        txtDlg.setContentText(content);

        Optional<String> result = txtDlg.showAndWait();

        result.ifPresent(input -> controller.callback(input, type));
    }
}

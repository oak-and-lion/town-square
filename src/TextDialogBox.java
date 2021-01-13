import java.util.Optional;

import javafx.scene.control.TextInputDialog;

public class TextDialogBox {
    private String title;
    private String headerText;
    private String content;
    private SampleController controller;

    public TextDialogBox(String title, String headerText, String content, SampleController controller) {
        this.title = title;
        this.headerText = headerText;
        this.content = content;
        this.controller = controller;
    }

    public void show() {
        var txtDlg = new TextInputDialog();
        txtDlg.setTitle(title);
        txtDlg.setHeaderText(headerText);
        txtDlg.setContentText(content);

        Optional<String> result = txtDlg.showAndWait();

        result.ifPresent(input -> {
            controller.processInvitation(input);
        });
    }
}

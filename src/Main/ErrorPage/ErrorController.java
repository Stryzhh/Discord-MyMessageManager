package Main.ErrorPage;

import Main.Config;
import Main.Neutral;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ErrorController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private ImageView icon;
    @FXML
    private ImageView minimizeIcon;
    @FXML
    private ImageView closeIcon;
    @FXML
    private Label title;
    @FXML
    private Label description;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Config.errorPanel = window;
        title.setText(Config.errorTitle);
        description.setText(Config.errorDescription);
        icon.setImage(new Image(new File("images\\something-wrong.png").toURI().toString()));
        minimizeIcon.setImage(new Image(new File("images\\minimize.png").toURI().toString()));
        closeIcon.setImage(new Image(new File("images\\grey-cross.png").toURI().toString()));
    }

    public void drag() {
        Neutral.dragWindow(window);
    }

    public void minimize() {
        Neutral.minimize(window);
    }

    /**
     * This method disposes the frame.
     */
    public final void dispose() {
        Stage thisWindow = (Stage) window.getScene().getWindow();
        thisWindow.close();
    }

}

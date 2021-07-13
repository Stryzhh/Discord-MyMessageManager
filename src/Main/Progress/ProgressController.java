package Main.Progress;

import Main.Config;
import Main.Neutral;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ProgressController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private ImageView minimizeIcon;
    @FXML
    private ImageView closeIcon;
    @FXML
    private Label status;
    @FXML
    private ListView<String> list;
    @FXML
    private ProgressBar progress;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //sets stylesheets and images
        progress.getStylesheets().add(getClass().getResource("progress-bar.css").toString());
        list.getStylesheets().add(getClass().getResource("list.css").toString());
        minimizeIcon.setImage(new Image(new File("images\\minimize.png").toURI().toString()));
        closeIcon.setImage(new Image(new File("images\\grey-cross.png").toURI().toString()));

        Config.progress = new ProgressController();
        Config.deletedList = list;
        Config.status = status;
        Config.progressBar = progress;
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
        Config.cancel = true;
        Stage thisWindow = (Stage) window.getScene().getWindow();
        thisWindow.close();
    }

}

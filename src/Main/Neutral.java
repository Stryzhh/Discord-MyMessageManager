package Main;

import Main.ErrorPage.ERRORS;
import Main.ErrorPage.ErrorController;
import Main.MessageManager.ManagerController;
import java.io.IOException;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Neutral {

    public static void dragWindow(AnchorPane window) {
        Stage thisWindow = (Stage) window.getScene().getWindow();

        window.setOnMousePressed(pressEvent -> window.setOnMouseDragged(dragEvent -> {
            thisWindow.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
            thisWindow.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
        }));
    }

    public static void changeWindow(String fxml, AnchorPane window) throws IOException {
        Parent next = FXMLLoader.load(Objects.requireNonNull(Neutral.class
                .getClassLoader().getResource(fxml)));
        Stage thisWindow = (Stage) window.getScene().getWindow();
        Stage nextWindow = new Stage();

        nextWindow.initStyle(StageStyle.UNDECORATED);
        nextWindow.setScene(new Scene(next, 340, 450));
        nextWindow.getIcons().add(new javafx.scene.image.Image("icon.png"));
        nextWindow.show();
        thisWindow.close();
    }

    public static void error(String title, String error) throws IOException {
        Config.errorTitle = title;
        Config.errorDescription = error;
        openErrorPage();
    }

    private static void openErrorPage() throws IOException {
        Parent startWindow = FXMLLoader.load(Objects.requireNonNull(Neutral.class
                .getClassLoader().getResource("Main/ErrorPage/error.fxml")));

        Stage window = new Stage();
        window.setAlwaysOnTop(true);
        window.initStyle(StageStyle.UNDECORATED);
        window.getIcons().add(new javafx.scene.image.Image("icon.png"));
        window.setScene(new Scene(startWindow, 360, 240));
        window.show();
    }

    public static void showGIF(String gif, String title) throws IOException {
        Config.gif = gif;
        Config.gifTitle = title;
        openGIF();
    }

    private static void openGIF() throws IOException {
        Parent startWindow = FXMLLoader.load(Objects.requireNonNull(Neutral.class
                .getClassLoader().getResource("Main/ShowGIF/show.fxml")));

        Stage window = new Stage();
        window.setAlwaysOnTop(true);
        window.initStyle(StageStyle.UNDECORATED);
        window.getIcons().add(new javafx.scene.image.Image("icon.png"));
        window.setScene(new Scene(startWindow, 354, 240));
        window.show();
    }

    public static void openProgress() throws IOException {
        Parent startWindow = FXMLLoader.load(Objects.requireNonNull(Neutral.class
                .getClassLoader().getResource("Main/Progress/progress.fxml")));

        Stage window = new Stage();
        window.initStyle(StageStyle.UNDECORATED);
        window.getIcons().add(new javafx.scene.image.Image("icon.png"));
        window.setScene(new Scene(startWindow, 280, 310));
        window.show();
    }

    /**
     * This method simply converts a string to a Long.
     *
     * @param text the passed string value
     * @return Returns the converted Long
     */
    public static long parseLong(final String text) throws IOException {
        long newLong = 0;
        try {
            if (!text.equals("")) {
                newLong = Long.parseLong(text);
            }
        } catch (Exception ex) {
            if (Config.errorPanel == null || !Config.errorPanel.getScene().getWindow().isShowing()) {
                Neutral.error(ERRORS.ERROR_TITLE_DATA_FORMAT,
                        ERRORS.ERROR_MESSAGE_FORMAT);
            }
            ManagerController.setConfig = false;
        }

        return newLong;
    }

    public static void minimize(AnchorPane window) {
        Stage thisWindow = (Stage) window.getScene().getWindow();
        thisWindow.setIconified(true);
    }
}

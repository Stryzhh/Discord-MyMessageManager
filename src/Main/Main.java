package Main;

import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage window) throws Exception{
        Parent startWindow = FXMLLoader.load(Objects.requireNonNull(getClass()
                .getClassLoader().getResource("Main/MessageManager/manager.fxml")));

        window.initStyle(StageStyle.UNDECORATED);
        window.setScene(new Scene(startWindow, 340, 450));
        window.show();
    }

}

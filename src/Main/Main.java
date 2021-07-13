package Main;

import java.awt.*;
import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.*;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage window) throws Exception{
        Parent startWindow = FXMLLoader.load(Objects.requireNonNull(getClass()
                .getClassLoader().getResource("Main/MessageManager/manager.fxml")));

        //adds tray icon
        PopupMenu popupMenu = new PopupMenu();
        ImageIcon logo = new ImageIcon("src/icon.png");
        Image image = logo.getImage();

        SystemTray tray = SystemTray.getSystemTray();
        Image trayImage = image.getScaledInstance(tray.getTrayIconSize().width,
                tray.getTrayIconSize().height, java.awt.Image.SCALE_SMOOTH);
        TrayIcon trayIcon = new TrayIcon(trayImage, "CredLock", popupMenu);

        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.addActionListener(e -> tray.remove(trayIcon));
        exitMenuItem.addActionListener(e -> System.exit(0));
        popupMenu.add(exitMenuItem);
        tray.add(trayIcon);

        window.initStyle(StageStyle.UNDECORATED);
        window.setScene(new Scene(startWindow, 340, 450));
        window.getIcons().add(new javafx.scene.image.Image("icon.png"));
        window.show();
    }

}

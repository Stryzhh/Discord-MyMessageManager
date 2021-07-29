package Main.MessageManager;

import Main.Config;
import Main.Neutral;
import Main.Requests;
import com.jfoenix.controls.JFXButton;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import com.jfoenix.controls.JFXCheckBox;
import javax.imageio.ImageIO;

public class ManagerController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private AnchorPane outline;
    @FXML
    private ImageView authorizationIcon;
    @FXML
    private ImageView authorIcon;
    @FXML
    private ImageView guildIcon;
    @FXML
    private ImageView channelIcon;
    @FXML
    private ImageView minimizeIcon;
    @FXML
    private ImageView closeIcon;
    @FXML
    private ImageView avatar;
    @FXML
    private PasswordField authorizePassword;
    @FXML
    private TextField authorizeText;
    @FXML
    private TextField author;
    @FXML
    private TextField guild;
    @FXML
    private TextField channel;
    @FXML
    private JFXCheckBox show;
    @FXML
    private JFXButton proceed;
    @FXML
    private Label username;
    public static boolean setConfig = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        authorizationIcon.setImage(new Image(new File("images\\authentication.png").toURI().toString()));
        authorIcon.setImage(new Image(new File("images\\author.png").toURI().toString()));
        guildIcon.setImage(new Image(new File("images\\guild.png").toURI().toString()));
        channelIcon.setImage(new Image(new File("images\\channel.png").toURI().toString()));
        minimizeIcon.setImage(new Image(new File("images\\minimize.png").toURI().toString()));
        closeIcon.setImage(new Image(new File("images\\grey-cross.png").toURI().toString()));
        avatar.setImage(new Image(new File("images\\not_found.png").toURI().toString()));

        if (!Config.savedAuthorization.equals("")) {
            authorizePassword.setText(Config.savedAuthorization);
            author.setText(Config.savedAuthorID);
            guild.setText(Config.savedGuildID);
            channel.setText(Config.savedChannelID);
        }

        authorizeText.setVisible(false);
        show.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old, Boolean newV) -> {
            if (show.isSelected()) {
                authorizeText.setText(authorizePassword.getText());
                authorizeText.setVisible(true);
                authorizePassword.setVisible(false);
                authorizePassword.setText("");
            } else {
                authorizePassword.setText(authorizeText.getText());
                authorizePassword.setVisible(true);
                authorizeText.setVisible(false);
                authorizeText.setText("");
            }
        });

        authorizePassword.textProperty().addListener(e -> findProfile());
        authorizeText.textProperty().addListener(e -> findProfile());
        author.textProperty().addListener(e -> findProfile());

        authorizePassword.textProperty().addListener(e -> proceedEnabled());
        authorizeText.textProperty().addListener(e -> proceedEnabled());
        author.textProperty().addListener(e -> proceedEnabled());
        channel.textProperty().addListener(e -> proceedEnabled());
        guild.textProperty().addListener(e -> proceedEnabled());
    }

    private void proceedEnabled() {
        boolean active = (!authorizePassword.getText().equals("") || !authorizeText.getText().equals(""))
                && !author.getText().equals("") && (!guild.getText().equals("") || !channel.getText().equals(""));
        proceed.setDisable(!active);
    }

    private void findProfile() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest getRequest;
            if (show.isSelected()) {
                getRequest = HttpRequest.newBuilder().uri(URI.create(
                        "https://discord.com/api/v9/users/" + author.getText())).
                        header("authorization", String.valueOf(authorizeText.getText())).GET().build();
            } else {
                getRequest = HttpRequest.newBuilder().uri(URI.create(
                        "https://discord.com/api/v9/users/" + author.getText())).
                        header("authorization", String.valueOf(authorizePassword.getText())).GET().build();
            }

            ArrayList<String> data = client.sendAsync(getRequest, HttpResponse.BodyHandlers.ofString()).
                    thenApply(HttpResponse::body).thenApply(Requests::parseUser).join();
            URL url = new URL("https://cdn.discordapp.com/avatars/" + author.getText()
                    + "/" + data.get(1) + ".png?size=64");
            BufferedImage pic = ImageIO.read(url);
            Image profile = SwingFXUtils.toFXImage(pic, null);

            avatar.setImage(profile);
            username.setText(data.get(0));
            outline.setVisible(true);
        } catch (Exception ex) {
            avatar.setImage(new Image(new File("images\\not_found.png").toURI().toString()));
            username.setText("Not Found...");
            outline.setVisible(false);
        }
    }

    public void setConfig() throws IOException {
        Config.request = new Requests();
        Config.cancel = false;
        String code = "";
        setConfig = true;

        if (!authorizePassword.getText().equals("")) {
            code = authorizePassword.getText();
        } else if (!authorizeText.getText().equals("")) {
            code = authorizeText.getText();
        }

        Config.code = code;
        Config.authorID = Neutral.parseLong(author.getText());
        Config.guildID = Neutral.parseLong(guild.getText());
        Config.channelID = Neutral.parseLong(channel.getText());
        Config.savedAuthorization = code;
        Config.savedAuthorID = author.getText();
        Config.savedGuildID = guild.getText();
        Config.savedChannelID = channel.getText();

        if (Config.guildID != 0 && Config.channelID == 0) {
            Config.messages = new ArrayList<>();
            Config.channelIDs = new ArrayList<>();

            ArrayList<Long> channels = Config.request.GET_CHANNELS();
            for (Long id : channels) {
                Config.channelIDs.add(id);
                Config.channelID = id;
                Config.request.GET_ALL_MESSAGES();
            }
            Config.request.BEGIN(false);
        } else if (setConfig) {
            Neutral.changeWindow("Main/DeleteOptions/options.fxml", window);
        }
    }

    public void openAuthorization() throws IOException {
        Neutral.showGIF("gifs\\authorization.gif", "Authorization Code");
    }

    public void openAuthor() throws IOException {
        Neutral.showGIF("gifs\\author.gif", "Author ID");
    }

    public void openGuild() throws IOException {
        Neutral.showGIF("gifs\\guild.gif", "Guild ID");
    }

    public void openChannel() throws IOException {
        Neutral.showGIF("gifs\\channel.gif", "Channel ID");
    }

    public void drag() {
        Neutral.dragWindow(window);
    }

    public void minimize() {
        Neutral.minimize(window);
    }

    public void exitApplication() {
        System.exit(1);
    }

}

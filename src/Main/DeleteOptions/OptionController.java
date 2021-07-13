package Main.DeleteOptions;

import Main.CalendarWindow;
import Main.Config;
import Main.ErrorPage.ERRORS;
import Main.Neutral;
import Main.Requests;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class OptionController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private ImageView sortIcon;
    @FXML
    private ImageView minimizeIcon;
    @FXML
    private ImageView backIcon;
    @FXML
    private ImageView closeIcon;
    @FXML
    private TextField start;
    @FXML
    private TextField end;
    @FXML
    private JFXCheckBox bulk;
    @FXML
    private JFXCheckBox date;
    @FXML
    private JFXCheckBox message;
    @FXML
    private JFXButton btnCalendarS;
    @FXML
    private JFXButton btnCalendarE;
    @FXML
    private ImageView startCalendar;
    @FXML
    private ImageView endCalendar;
    private CalendarWindow calendarS;
    private CalendarWindow calendarE;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Config.request = new Requests();
        Config.window = window;
        Config.txtStart = start;
        Config.txtEnd = end;

        sortIcon.setImage(new Image(new File("images\\not_required.png").toURI().toString()));
        minimizeIcon.setImage(new Image(new File("images\\minimize.png").toURI().toString()));
        startCalendar.setImage(new Image(new File("images\\date.png").toURI().toString()));
        endCalendar.setImage(new Image(new File("images\\date.png").toURI().toString()));
        backIcon.setImage(new Image(new File("images\\back.png").toURI().toString()));
        closeIcon.setImage(new Image(new File("images\\grey-cross.png").toURI().toString()));

        bulk.selectedProperty().addListener(e -> checkboxChanger(bulk, date, message));
        date.selectedProperty().addListener(e -> checkboxChanger(date, message, bulk));
        message.selectedProperty().addListener(e -> checkboxChanger(message, bulk, date));

        calendarS = new CalendarWindow(Config.txtStart, true);
        calendarE = new CalendarWindow(Config.txtEnd, false);

        start.setPromptText("Not Required");
        start.setDisable(true);
        end.setPromptText("Not Required");
        end.setDisable(true);
    }

    public void proceed() throws IOException {
        setAttributes();
        Config.cancel = false;

        try {
            int current;
            if (Config.STATE == Config.CURRENT_STATE.Message) {
                if (Config.startID == 0 && !(Config.endID == 0)) {
                    ArrayList<Long> messages = Config.request.GET("?before=" + Config.endID + "&");
                    do {
                        current = messages.size();
                        long lastLong = Config.nextSet.getJSONObject(Config.nextSet.length() - 1).getLong("id");
                        messages.addAll(Config.request.GET("?before=" + lastLong + "&"));
                    } while (current != messages.size());

                    Config.messages = messages;
                    Config.amountOfMessages = messages.size();
                    Config.request.BEGIN(true);
                } else if (!(Config.startID == 0) && Config.endID == 0) {
                    Config.request.GET_MESSAGES_BETWEEN(Config.request.GET("?").get(0), Config.startID);
                } else if (!(Config.startID == 0)) {
                    Config.request.GET_MESSAGES_BETWEEN(Config.endID, Config.startID);
                }
            }

            if (Config.STATE == Config.CURRENT_STATE.Date) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                if (Config.startDate != null) {
                    Config.startDateString = dateFormat.format(Config.startDate);
                }
                if (Config.endDate != null) {
                    Config.endDateString = dateFormat.format(Config.endDate);
                }

                ArrayList<ArrayList<Long>> data;
                if (Config.startDate != null && Config.endDate == null) {
                    data = Config.request.GET_TIMED("?");
                    while (!Config.foundStart) {
                        long lastLong = Config.nextSet.getJSONObject(
                                Config.nextSet.length() - 1).getLong("id");
                        data.addAll(Config.request.GET_TIMED("?before=" + lastLong + "&"));
                    }

                    Long startID = data.get(0).get(data.get(0).size() - 1);
                    Long lastMessage = Config.request.GET("?").get(0);
                    Config.request.GET_MESSAGES_BETWEEN(lastMessage, startID);
                } else if (Config.startDate == null && Config.endDate != null) {
                    data = Config.request.GET_TIMED("?");
                    while (!Config.foundEnd) {
                        long lastLong = Config.nextSet.getJSONObject(
                                Config.nextSet.length() - 1).getLong("id");
                        data.addAll(Config.request.GET_TIMED("?before=" + lastLong + "&"));
                    }

                    Long endID = data.get(1).get(0);
                    ArrayList<Long> messages = Config.request.GET("?before=" + endID + "&");
                    do {
                        current = messages.size();
                        long lastLong = Config.nextSet.getJSONObject(
                                Config.nextSet.length() - 1).getLong("id");
                        messages.addAll(Config.request.GET("?before=" + lastLong + "&"));
                    } while (current != messages.size());

                    Config.messages = messages;
                    Config.amountOfMessages = messages.size();
                    Config.request.BEGIN(true);
                } else if (Config.startDate != null) {
                    data = Config.request.GET_TIMED("?");
                    while (!Config.foundStart && !Config.foundEnd) {
                        long lastLong = Config.nextSet.getJSONObject(
                                Config.nextSet.length() - 1).getLong("id");
                        data.addAll(Config.request.GET_TIMED("?before=" + lastLong + "&"));
                    }

                    Long startID = data.get(0).get(data.get(0).size() - 1);
                    Long endID = data.get(1).get(0);
                    Config.request.GET_MESSAGES_BETWEEN(endID, startID);
                }
            }

            if (Config.STATE == Config.CURRENT_STATE.Bulk) {
                if (Config.channelID != 0) {
                    Config.messages = new ArrayList<>();
                    Config.request.GET_ALL_MESSAGES();
                    Config.request.BEGIN(true);
                } else if (Config.guildID != 0) {
                    Config.messages = new ArrayList<>();
                    Config.channelIDs = new ArrayList<>();

                    ArrayList<Long> channels = Config.request.GET_CHANNELS();
                    for (Long id : channels) {
                        Config.channelIDs.add(id);
                        Config.channelID = id;
                        Config.request.GET_ALL_MESSAGES();
                    }
                    Config.request.BEGIN(false);
                }
            }
        } catch (Exception ex) {
            if (Config.errorPanel == null || !Config.errorPanel.getScene().getWindow().isShowing()) {
                Neutral.error(ERRORS.ERROR_TITLE_SOMETHING_WENT_WRONG, ERRORS.ERROR_MESSAGE_DETAILS);
            }
        }
    }

    private void setAttributes() throws IOException {
        if (Config.STATE == Config.CURRENT_STATE.Message) {
            Config.startID = Neutral.parseLong(start.getText());
            Config.endID = Neutral.parseLong(end.getText());
        }
    }

    public void showGIF() throws IOException {
        if (sortIcon.getImage().getUrl().contains("date.png")) {
            Neutral.showGIF("gifs\\range.gif", "Date ID");
        } else if (sortIcon.getImage().getUrl().contains("message.png")) {
            Neutral.showGIF("gifs\\message.gif", "Message ID");
        }
    }

    private void checkboxChanger(JFXCheckBox box, JFXCheckBox box1, JFXCheckBox box2) {
        if (box.isSelected()) {
            box1.setSelected(false);
            box2.setSelected(false);
        } else if (box1.isSelected()) {
            box.setSelected(false);
            box2.setSelected(false);
        } else {
            box2.setSelected(true);
            box.setSelected(false);
            box1.setSelected(false);
        }

        if (bulk.isSelected()) {
            Config.STATE = Config.CURRENT_STATE.Bulk;
            sortIcon.setImage(new Image(new File("images\\not_required.png").toURI().toString()));

            start.setPromptText("Not Required");
            start.setDisable(true);
            start.setText("");
            end.setPromptText("Not Required");
            end.setDisable(true);
            end.setText("");

            btnCalendarS.setDisable(true);
            calendarS.dispose();
            btnCalendarE.setDisable(true);
            calendarE.dispose();
        } else if (date.isSelected()) {
            Config.STATE = Config.CURRENT_STATE.Date;
            sortIcon.setImage(new Image(new File("images\\date.png").toURI().toString()));

            start.setPromptText("Start Date");
            start.setDisable(true);
            start.setText("");
            end.setPromptText("End Date");
            end.setDisable(true);
            end.setText("");

            btnCalendarS.setDisable(false);
            btnCalendarE.setDisable(false);
        } else {
            Config.STATE = Config.CURRENT_STATE.Message;
            sortIcon.setImage(new Image(new File("images\\message.png").toURI().toString()));

            start.setPromptText("Start Message ID");
            start.setEditable(true);
            start.setDisable(false);
            start.setText("");
            end.setEditable(true);
            end.setPromptText("End Message ID");
            end.setDisable(false);
            end.setText("");

            btnCalendarS.setDisable(true);
            calendarS.dispose();
            btnCalendarE.setDisable(true);
            calendarE.dispose();
        }
    }

    public void openStartCalendar() {
        calendarS.setVisible(true);
        calendarE.setVisible(false);
    }

    public void openEndCalendar() {
        calendarE.setVisible(true);
        calendarS.setVisible(false);
    }

    public void drag() {
        Neutral.dragWindow(window);
    }

    public void minimize() {
        Neutral.minimize(window);
    }

    public void back() throws IOException {
        Neutral.changeWindow("Main/MessageManager/manager.fxml", window);
    }

    public void exitApplication() {
        System.exit(1);
    }

}

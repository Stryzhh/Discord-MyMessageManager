package Main;

import Main.ErrorPage.ERRORS;
import Main.Progress.ProgressController;
import java.util.ArrayList;
import java.util.Date;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.json.JSONArray;

public class Config {

    public static AnchorPane window;
    public static ProgressController progress;
    public static Requests request;
    public static JSONArray nextSet;
    public static ArrayList<Long> messages;
    public static ArrayList<Long> channelIDs;
    public static ListView<String> deletedList;
    public static Label status;
    public static String code;
    public static Object STATE = CURRENT_STATE.Bulk;
    public static Date startDate = null;
    public static Date endDate = null;
    public static long authorID;
    public static long guildID;
    public static long channelID;
    public static long startID;
    public static long endID;
    public static boolean foundStart = false;
    public static boolean foundEnd = false;
    public static double retry;
    public static int amountOfMessages;
    public static TextField txtStart;
    public static TextField txtEnd;
    public static String startDateString = null;
    public static String endDateString = null;
    public static ProgressBar progressBar;
    public static String savedAuthorization = "";
    public static String savedAuthorID = "";
    public static String savedGuildID = "";
    public static String savedChannelID = "";
    public static String gif = "";
    public static String gifTitle = "";
    public static String errorDescription = ERRORS.ERROR_TITLE_SOMETHING_WENT_WRONG;
    public static String errorTitle = ERRORS.ERROR_MESSAGE_DETAILS;
    public static AnchorPane errorPanel = null;
    public static boolean cancel = false;

    public enum CURRENT_STATE {
        /**
         * identifies the user is deleting using message parameters.
         */
        Message(),
        /**
         * identifies the user is deleting using date parameters.
         */
        Date(),
        /**
         * identifies the user is deleting using no parameters.
         */
        Bulk()
    }

}

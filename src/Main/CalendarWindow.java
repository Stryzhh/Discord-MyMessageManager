package Main;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.TextField;
import javax.swing.JFrame;
import com.mindfusion.scheduling.Calendar;
import com.mindfusion.scheduling.ThemeType;
import com.mindfusion.common.DateTime;

/**
 * Calendar.java.
 * @version 1.0.0
 * This class simply implements a calendar, the user can select a start
 * and/or an end date to delete messages.
 * @author Dafydd-Rhys Maund
 */
public class CalendarWindow extends JFrame {

    /** The UI serial version. */
    private static final long serialVersionUID = 1L;
    /** An instance of the calendar UI. */
    private final Calendar calendar = new Calendar();
    /** The string value of the date. */
    private String dateString;

    /**
     * This method shows a calendar window when the calendar icon is pressed.
     * @param field the field where the date is displayed
     * @param start gathers whether we're identifying the start or end date
     */
    public CalendarWindow(final TextField field, final Boolean start) {
        Point2D txtPoint;
        Bounds windowPoint = Config.window.localToScene(Config.window.getBoundsInParent());
        if (start) {
            txtPoint = Config.txtStart.localToScene(0.0, 0.0);
        } else {
            txtPoint = Config.txtEnd.localToScene(0.0, 0.0);
        }
        requestFocus();

        calendar.setTheme(ThemeType.Standard);
        calendar.requestFocus();
        setUndecorated(true);
        setSize(235, 200);
        setLocation((int) ((int) txtPoint.getX() + ((int) windowPoint.getMaxX() * 3.45)),
                (int) ((int) txtPoint.getY() + (int) windowPoint.getMaxY() / 1.9));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowIconified(WindowEvent wEvt) {
                dispose();
            }
            @Override
            public void windowDeactivated(WindowEvent wEvt) {
                dispose();
            }
        });

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(calendar, BorderLayout.CENTER);

        calendar.addMouseListener(new MouseAdapter() {
            public void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() == 2) {
                    calendar.getSelection().reset();
                    DateTime pointedDate = calendar.getDateAt(e.getX(), e.getY());
                    dateString = pointedDate.getDay() + "-" + pointedDate.getMonth() + "-" + pointedDate.getYear();

                    try {
                        Date date = new SimpleDateFormat("dd-MM-yyyy").parse(dateString);
                        if (start) {
                            Config.startDate = date;
                        } else {
                            Config.endDate = date;
                        }
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                    }

                    field.setText(dateString);
                    dispose();
                }
            }
        });
    }
}

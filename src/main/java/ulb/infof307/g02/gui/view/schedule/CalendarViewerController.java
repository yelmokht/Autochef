package ulb.infof307.g02.gui.view.schedule;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.apache.commons.lang3.ArrayUtils;
import ulb.infof307.g02.gui.view.ViewerController;

import java.text.DateFormatSymbols;
import java.util.List;

public class CalendarViewerController extends ViewerController<CalendarViewerController.Listener> {

    @FXML
    private GridPane gridpane;

    @FXML
    private Label monthYear;

    private final Pane[] panes = new Pane[42];

    private final Label[] calendarDates = new Label[42];

    private final String[] months = new DateFormatSymbols().getMonths();

    @FXML
    void backToPreviousMonthOnCLicked() {
        listener.backToPreviousMonth();
    }

    @FXML
    void goToNextMonthOnClicked() {
        listener.goToNextMonth();
    }

    public void initialiseGrid(){
        gridpane.getChildren().forEach(dayBox -> {
            if(dayBox instanceof Pane) {
                setOnClickEventDayBox(dayBox);

                // get first child of pane (represents the date on the day box)
                Label dayDate = (Label) ((Pane) dayBox).getChildren().get(0);
                if (GridPane.getColumnIndex(dayBox) == null) {
                    panes[(GridPane.getRowIndex(dayBox) - 1) * 7] = (Pane) dayBox;
                    calendarDates[(GridPane.getRowIndex(dayBox) - 1) * 7] = dayDate;
                } else {
                    panes[(GridPane.getRowIndex(dayBox) - 1) * 7 + GridPane.getColumnIndex(dayBox)] = (Pane) dayBox;
                    calendarDates[(GridPane.getRowIndex(dayBox) - 1) * 7 + GridPane.getColumnIndex(dayBox)] = dayDate;
                }
            }
        });
    }

    public void setOnClickEventDayBox(Node dayBox) {
        dayBox.setOnMouseClicked(event -> {
            if(dayBox instanceof Pane){
                Pane pane = (Pane) dayBox;
                List<Node> children = pane.getChildren();
                Label dayDate = (Label) children.get(0);
                listener.setupDate(ArrayUtils.indexOf(calendarDates, dayDate));
            }
        });
    }

    public void setColorDate(int index, int pastindex){
        panes[pastindex].setStyle("-fx-background-color: rgba(255,255,255,0)");
        panes[index].setStyle("-fx-background-color: rgba(47,101,40,0.6)");
    }

    public void resetColorDate(int index){
        for(Pane pane:panes){
            pane.setStyle("-fx-background-color: rgba(255,255,255,0)");
        }
        panes[index].setStyle("-fx-background-color: rgba(47,101,40,0.6)");
    }

    public void setMonthYear(int month){
        String monthStr = months[month];
        monthYear.setText(monthStr.substring(0, 1).toUpperCase() + monthStr.substring(1));
    }

    public void setCalendarDate(String date, int index){
        calendarDates[index].setText(date);
    }

    public void setPaneGray(int index){
        panes[index].setStyle("-fx-background-color: rgba(86,76,76,0.4)");
    }

    public interface Listener extends ViewerController.ViewerListener {

        void backToPreviousMonth();

        void goToNextMonth();

        void setupDate(int index);

    }

}

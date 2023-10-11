package ulb.infof307.g02.gui.controller.schedule;

import ulb.infof307.g02.gui.view.schedule.CalendarViewerController;

import java.util.Calendar;
import java.util.Date;

public class CalendarController implements CalendarViewerController.Listener{

    private final CalendarViewerController calendarViewerController;
    private Date date;
    private int indexDate = 0;
    private final Date[] dates = new Date[42];

    private final Listener listener;

    public CalendarController(Listener listener, CalendarViewerController calendarViewerController) {
        this.listener = listener;
        this.calendarViewerController = calendarViewerController;
        this.date = new Date();

        calendarViewerController.initialiseGrid();
        calendarViewerController.setListener(this);
        initializeCalendar();
    }

    private void initializeCalendar(){
        int month = date.getMonth();
        Calendar calendar = Calendar.getInstance();
        Date temp = new Date(date.getYear(),month,1);
        calendar.setTime(temp);
        int firstDayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if(firstDayIndex == -1){
            firstDayIndex = 6;
        }
        int lastDayIndex = firstDayIndex + calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for(int i=0; i<calendar.getActualMaximum(Calendar.DAY_OF_MONTH);i++){
            if(date.getDate()==i+1){
                dates[i+firstDayIndex] = date;
                indexDate = i+firstDayIndex;
            }
            dates[i+firstDayIndex] = new Date(temp.getYear(),month,i+1);
        }
        calendarViewerController.resetColorDate(indexDate);
        temp.setMonth(month-1);
        calendar.setTime(temp);
        for(int i=0; i<firstDayIndex;i++) {
            dates[i] = new Date (temp.getYear(),temp.getMonth(),calendar.getActualMaximum(Calendar.DAY_OF_MONTH) -firstDayIndex+1+i);
            calendarViewerController.setPaneGray(i);
        }
        temp.setMonth(month+1);
        for(int i=lastDayIndex; i<42;i++){
            dates[i] = new Date(temp.getYear(),temp.getMonth(),i+1-lastDayIndex);
            calendarViewerController.setPaneGray(i);
        }
        for(int i=0;i<dates.length;i++){
            calendarViewerController.setCalendarDate(String.valueOf(dates[i].getDate()),i);
        }
        calendarViewerController.setMonthYear(month);
        listener.setupDate(date);
    }

    public void setupDate(int index){
        if (date.getMonth() == dates[index].getMonth()){
            calendarViewerController.setColorDate(index, indexDate);
            indexDate = index;
            date = dates[index];
            listener.setupDate(date);
        }else{
            date = dates[index];
            initializeCalendar();
        }
    }

    public void backToPreviousMonth(){
        date.setMonth(date.getMonth() - 1);
        initializeCalendar();
    }

    public void goToNextMonth(){
        date.setMonth(date.getMonth() + 1);
        initializeCalendar();
    }

    public interface Listener {

        void setupDate(Date date);

    }

}


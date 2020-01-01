package kr.hee.kwnoti.schedule_activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SixRowCalendar {
    private static final int DAYS_OF_WEEK = 7;
    private static final int ROWS_OF_CALENDAR = 6;

    private Calendar calendar;
    private ArrayList<Integer> calendarData;

    int maxDateOfCurrent;
    int dayOfPrevMonthLast;
    int dayOfNextMonthFirst;

    /**
     * Callback interface for month changed
     */
    public interface OnMonthChanged {
        void monthChanged();
    }

    /**
     * Exception for callback when it is empty
     */
    public class NoCallbackException extends RuntimeException {
        NoCallbackException() {
            super("No callback is here.");
        }
    }

    private OnMonthChanged onMonthChanged = null;

    /**
     *  Constructor
     */
    public SixRowCalendar(OnMonthChanged onMonthChanged) {
        if (onMonthChanged == null) throw new NoCallbackException();

        this.calendar = Calendar.getInstance(Locale.KOREA);
        this.onMonthChanged = onMonthChanged;
        calendarData = new ArrayList<>();

        makeCalendar();
    }

    /* Set calendar data */
    public void makeCalendar() {
        calendarData.clear();

        calendar.set(Calendar.DATE, 1);
        maxDateOfCurrent = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        dayOfPrevMonthLast = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        dayOfNextMonthFirst = ROWS_OF_CALENDAR*DAYS_OF_WEEK - (dayOfPrevMonthLast+maxDateOfCurrent);

        // put date
        putPrevMonth((Calendar)calendar.clone());
        putCurrMonth(calendar);
        putNextMonth();

        // listener
        onMonthChanged.monthChanged();
    }

    /* Put date in ArrayList */
    private void putPrevMonth(Calendar cal) {
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-1);
        int maxDate = cal.getActualMaximum(Calendar.DATE);
        int maxOffsetDate = maxDate - dayOfPrevMonthLast;
        for (int i = 1; i<=dayOfPrevMonthLast; i++) {
            calendarData.add(++maxOffsetDate);
        }
    }
    private void putCurrMonth(Calendar cal) {
        for (int i=1; i<=cal.getActualMaximum(Calendar.DATE); i++)
            calendarData.add(i);
    }
    private void putNextMonth() {
        for (int i=1; i<=dayOfNextMonthFirst; i++)
            calendarData.add(i);
    }

    /* Change month */
    public void setMonthToNext() {
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+1);
        makeCalendar();
    }
    public void setMonthToPrev() {
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-1);
        makeCalendar();
    }

    public int getDate(int index) {
        return calendarData.get(index);
    }
    public int getCalendarLength() {
        return calendarData.size();
    }
}

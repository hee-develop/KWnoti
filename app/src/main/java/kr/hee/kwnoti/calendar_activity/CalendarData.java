package kr.hee.kwnoti.calendar_activity;

/** 캘린더 데이터를 담는 클래스 */
public class CalendarData {
    public String  year,
            startMonth,
            startDate,
            endMonth,
            endDate,
            content;
    public CalendarData(String year, String month, String date, String content) {
        this.year  = year;
        this.startMonth = month;
        this.startDate  = date;
        this.endMonth   = startMonth;
        this.endDate    = startDate;
        this.content = content;
    }
    public CalendarData(String year, String startMonth, String startDate,
                        String endMonth, String endDate, String content) {
        this.year  = year;
        this.startMonth = startMonth;
        this.startDate  = startDate;
        this.endMonth   = endMonth;
        this.endDate    = endDate;
        this.content = content;
    }
}

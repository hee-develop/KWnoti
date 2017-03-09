package kr.hee.kwnoti.calendar_activity;

/** 캘린더 데이터를 담는 클래스 */
class CalendarData {
    String  year,                   // 연도
            startMonth, startDate,  // 시작월/일
            endMonth,   endDate,    // 종료월/일(없는 경우 시작월/일과 동일)
            content;                // 내용

    CalendarData(String year, String month, String date, String content) {
        this.year  = year;
        this.startMonth = month;
        this.startDate  = date;
        this.endMonth   = startMonth;
        this.endDate    = startDate;
        this.content = content;
    }
    CalendarData(String year, String startMonth, String startDate,
                 String endMonth, String endDate, String content) {
        this.year  = year;
        this.startMonth = startMonth;
        this.startDate  = startDate;
        this.endMonth   = endMonth;
        this.endDate    = endDate;
        this.content = content;
    }
}

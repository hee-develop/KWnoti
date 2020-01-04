package kr.hee.kwnoti.schedule_activity;

public class ScheduleData {
    short startYear;
    short startMonth;
    short startDate;
    short endYear;
    short endMonth;
    short endDate;
    String scheduleData;

    public ScheduleData(short startYear, short startMonth, short startDate, short endYear, short endMonth, short endDate, String scheduleData) {
        this.startYear = startYear;
        this.startMonth = startMonth;
        this.startDate = startDate;
        this.endYear = endYear;
        this.endMonth = endMonth;
        this.endDate = endDate;
        this.scheduleData = scheduleData;
    }
    public ScheduleData(String[] start, String[] end, String content) {
        this.startYear = Short.parseShort(start[0]);
        if (start.length > 1) this.startMonth = Short.parseShort(start[1]);
        if (start.length > 2) this.startDate  = Short.parseShort(start[2]);

        this.endYear = Short.parseShort(end[0]);
        if (start.length > 1) this.endMonth = Short.parseShort(end[1]);
        if (start.length > 2) this.endDate  = Short.parseShort(end[2]);

        this.scheduleData = content;
    }
}

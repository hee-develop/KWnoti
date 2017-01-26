package kr.hee.kwnoti.calendar_activity;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/** 학사 일정 데이터를 관리하는 DB 클래스 */
public class CalendarDB {
    private SQLiteDatabase db;
    private final String DB_NAME = "Calendar";

    public CalendarDB(Context context) {
        db = context.openOrCreateDatabase(DB_NAME + ".db",
                SQLiteDatabase.CREATE_IF_NECESSARY, null);
        String query = "CREATE TABLE IF NOT EXISTS Calendar(" +
                "`year` text not null, `startMonth` text not null, " +
                "`startDate` text not null, `endMonth` text not null, " +
                "`endDate` text not null, `content` text not null);";
        /* 테이블 모양 구성 ------------------------------------
         * year         년   / 텍스트 / 길이(제한없음) / NOT NULL
         * startMonth   시작월/ 텍스트 / 길이(제한없음) / NOT NULL
         * startDate    시작일/ 텍스트 / 길이(제한없음) / NOT NULL
         * endMonth     종료월/ 텍스트 / 길이(제한없음) / NOT NULL
         * endDate      종료일/ 텍스트 / 길이(제한없음) / NOT NULL
         * content 내용  / 텍스트 / 길이(제한없음) / NOT NULL
         * -------------------------------------------------- */
        try {
            db.execSQL(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** DB에 데이터 추가 메소드
     * @param data     CalendarData 클래스
     * @return         추가됐는지 여부 */
    boolean addCalendar(CalendarData data) {
        try {
            db.execSQL("INSERT INTO " + DB_NAME +
                    " VALUES(\'" + data.year     + "\'," +
                    "\'" + data.startMonth    + "\'," +
                    "\'" + data.startDate     + "\'," +
                    "\'" + data.endMonth      + "\'," +
                    "\'" + data.endDate       + "\'," +
                    "\'" + data.content  + "\');");
        }
        catch (SQLException e) {
            return false;
        }
        return true;
    }

    /** DB 전체 삭제 메소드 */
    void cleanCalendar() {
        db.execSQL("DELETE FROM " + DB_NAME);
    }

    /** DB 데이터를 가져오는 메소드
     * @param calendars    데이터가 들어 갈 ArrayList. 포인터를 통해 데이터 제공 */
    void getCalendar(ArrayList<CalendarData> calendars) {
        if (calendars == null) return;

        Cursor cursor = db.rawQuery("SELECT * FROM " + DB_NAME, null);
        while (cursor.moveToNext()) {
            CalendarData data = new CalendarData(
                    cursor.getString(0), // 년
                    cursor.getString(1), // 시작월
                    cursor.getString(2), // 시작일
                    cursor.getString(3), // 종료월
                    cursor.getString(4), // 종료일
                    cursor.getString(5));// 내용
            calendars.add(data);
        }
        cursor.close();
    }
}

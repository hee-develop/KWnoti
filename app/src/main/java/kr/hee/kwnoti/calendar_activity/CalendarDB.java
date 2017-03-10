package kr.hee.kwnoti.calendar_activity;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import kr.hee.kwnoti.DatabaseHelper;

/** 학사 일정 데이터를 관리하는 DB 클래스 */
class CalendarDB extends DatabaseHelper {
    private final static String DB_NAME = "Calendar";

    CalendarDB(Context context) {
        super(DB_NAME, context);
    }

    /** DB 생성 메소드 */
    @Override public void createDB(Context context) {
        SQLiteDatabase db = context.openOrCreateDatabase(DB_NAME + ".db",
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

        // DB 설정
        setDB(db);
    }

    /** DB 데이터 추가 메소드 */
    @Override public boolean addToDB(Object value) {
        CalendarData data = (CalendarData)value;

        try {
            getDB().execSQL("INSERT INTO " + DB_NAME +
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

    /** DB 데이터를 가져오는 메소드 */
    @Override public void getData(Object calendars) {
        if (calendars == null) return;

        Cursor cursor = getDB().rawQuery("SELECT * FROM " + DB_NAME, null);
        while (cursor.moveToNext()) {
            CalendarData data = new CalendarData(
                    cursor.getString(0), // 년
                    cursor.getString(1), // 시작월
                    cursor.getString(2), // 시작일
                    cursor.getString(3), // 종료월
                    cursor.getString(4), // 종료일
                    cursor.getString(5));// 내용
            ((ArrayList<CalendarData>)calendars).add(data);
        }
        cursor.close();
    }
}

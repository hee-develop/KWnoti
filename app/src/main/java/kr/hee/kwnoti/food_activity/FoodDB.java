package kr.hee.kwnoti.food_activity;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import kr.hee.kwnoti.DatabaseHelper;

/** 학식 데이터를 관리하는 DB 클래스 */
class FoodDB extends DatabaseHelper {
    private final static String DB_NAME = "Food";

    FoodDB(Context context) {
        super(DB_NAME, context);
    }

    /** DB 생성 메소드 */
    @Override public void createDB(Context context) {
        SQLiteDatabase db = context.openOrCreateDatabase(DB_NAME + ".db",
                SQLiteDatabase.CREATE_IF_NECESSARY, null);
        String query = "CREATE TABLE IF NOT EXISTS Food(" +
                "`dayOfWeek` text not null," +
                "`title` text not null, `content` text not null);";
        /* 테이블 모양 구성 ------------------------------------
         * dayOfWeek    요일  / 텍스트 / 길이(제한없음) / NOT NULL
         * title        시간대 / 텍스트 / 길이(제한없음) / NOT NULL
         * content      식단  / 텍스트 / 길이(제한없음) / NOT NULL
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
        FoodData data = (FoodData)value;

        try {
            getDB().execSQL("INSERT INTO " + DB_NAME +
                    " VALUES(\'" + data.dayOfWeek   + "\'," +
                    "\'" + data.title               + "\'," +
                    "\'" + data.content             + "\');");
        }
        catch (SQLException e) {
            return false;
        }
        return true;
    }

    /** DB 데이터를 가져오는 메소드 */
    @Override public void getData(Object foods) {
        if (foods == null) return;

        Cursor cursor = getDB().rawQuery("SELECT * FROM " + DB_NAME, null);
        while (cursor.moveToNext()) {
            FoodData data = new FoodData(
                    cursor.getString(0), // 요일
                    cursor.getString(1), // 시간대
                    cursor.getString(2));// 식단
            ((ArrayList<FoodData>)foods).add(data);
        }
        cursor.close();
    }
}

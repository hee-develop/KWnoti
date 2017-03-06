package kr.hee.kwnoti.food_activity;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/** 학식 데이터를 관리하는 DB 클래스 */
class FoodDB {
    private SQLiteDatabase db;
    private final String DB_NAME = "Food";

    FoodDB(Context context) {
        db = context.openOrCreateDatabase(DB_NAME + ".db",
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
    }

    /** DB 메모리 누수 방지 */
    @Override protected void finalize() throws Throwable {
        super.finalize();
        if (db.isOpen())
            db.close();
    }

    /** DB에 데이터 추가 메소드
     * @param data    학식 데이터
     * @return        추가됐는지 여부
     */
    boolean addFood(FoodData data) {
        try {
            db.execSQL("INSERT INTO " + DB_NAME +
                    " VALUES(\'" + data.dayOfWeek   + "\'," +
                    "\'" + data.title               + "\'," +
                    "\'" + data.content             + "\');");
        }
        catch (SQLException e) {
            return false;
        }
        return true;
    }

    /** DB 전체 삭제 메소드 */
    void cleanFood() {
        db.execSQL("DELETE FROM " + DB_NAME);
    }

    /** DB 데이터를 가져오는 메소드
     * @param foods    데이터가 들어 갈 ArrayList. 포인터를 통해 데이터 제공 */
    void getFoods(ArrayList<FoodData> foods) {
        if (foods == null) return;

        Cursor cursor = db.rawQuery("SELECT * FROM " + DB_NAME, null);
        while (cursor.moveToNext()) {
            FoodData data = new FoodData(
                    cursor.getString(0), // 요일
                    cursor.getString(1), // 시간대
                    cursor.getString(2));// 식단
            foods.add(data);
        }
        cursor.close();
    }
}

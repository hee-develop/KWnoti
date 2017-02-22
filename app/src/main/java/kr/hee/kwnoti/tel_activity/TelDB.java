package kr.hee.kwnoti.tel_activity;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/** 전화번호 데이터를 관리하는 DB 클래스 */
class TelDB {
    private SQLiteDatabase db;
    private final String DB_NAME = "TelNumber";

    TelDB(Context context) {
        db = context.openOrCreateDatabase(DB_NAME + ".db",
                SQLiteDatabase.CREATE_IF_NECESSARY, null);
        String query = "CREATE TABLE IF NOT EXISTS " + DB_NAME + "(" +
                "`groupName` text not null," +
                "`departName` text not null," +
                "`telNumber` text not null);";
        /* 테이블 모양 구성 ----------------------------
         * groupName    부서 그룹   / 텍스트 / NOT NULL
         * departName   부서명     / 텍스트 / NOT NULL
         * telNumber    전화번호    / 텍스트 / NOT NULL
         * ------------------------------------------ */
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
     * @param data    전화번호 데이터
     * @return        추가됐는지 여부 */
    boolean addTel(TelData data) {
        try {
            db.execSQL("INSERT INTO " + DB_NAME + " VALUES(" +
                    "\'" + data.groupName + "\'," +
                    "\'" + data.departName + "\'," +
                    "\'" + data.telNumber + "\');");
        }
        catch (SQLException e) {
            return false;
        }
        return true;
    }

    /** DB 전체 삭제 메소드 */
    void cleanTel() {
        db.execSQL("DELETE FROM " + DB_NAME);
    }


    /** DB 검색 메소드
     * @return        찾은 데이터 */
    ArrayList<TelData> getTelNumber() {
        ArrayList<TelData> array = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DB_NAME, null);
        while (cursor.moveToNext()) {
            TelData telData = new TelData(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2)
            );
            array.add(telData);
        }
        cursor.close();
        return array;
    }

    /** DB 검색 메소드
     * @param find    찾고자 하는 문구
     * @return        찾은 데이터 */
    ArrayList<TelData> getTelNumber(String find) {
        ArrayList<TelData> array = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DB_NAME +
                " where groupName LIKE '%" + find + "%' OR departName LIKE '%" + find + "%' OR telNumber LIKE '%" + find + "%';", null);
        while (cursor.moveToNext()) {
            TelData telData = new TelData(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2)
            );
            array.add(telData);
        }
        cursor.close();
        return array;
    }
}

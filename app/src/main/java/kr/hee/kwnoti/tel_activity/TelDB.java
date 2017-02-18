package kr.hee.kwnoti.tel_activity;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/** 전화번호 데이터를 관리하는 DB 클래스 */
public class TelDB {
    private SQLiteDatabase db;
    private final String DB_NAME = "TelNumber";

    TelDB(Context context) {
        db = context.openOrCreateDatabase(DB_NAME + ".db",
                SQLiteDatabase.CREATE_IF_NECESSARY, null);
        String query = "CREATE TABLE IF NOT EXISTS Tel(" +
                "`groupName` text not null," +
                "`departName` text not null," +
                "`telNumber` text not null);";
        try {
            db.execSQL(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
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
                "where groupName=" + find + "or departName=" + find + "or telNumber=" + find, null);
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

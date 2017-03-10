package kr.hee.kwnoti.settings_activity;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/** 푸쉬 필터 데이터를 관리하는 DB 클래스 */
public class PushFilterDB {
    private SQLiteDatabase db;
    private final String DB_NAME = "PushFilter";

    public PushFilterDB(Context context) {
        // DB 파일 생성 및 테이블 생성
        db = context.openOrCreateDatabase(DB_NAME + ".db",
                SQLiteDatabase.CREATE_IF_NECESSARY, null);
        String query = "CREATE TABLE IF NOT EXISTS PushFilter(" +
                "`filterName` char(30) unique not null);";
        /* 테이블 모양 구성 ----------
         * filterName
         * 길이 : 한글 최대 15자 (30Byte)
         * 특성 : 유니크 키값(중복 불가)
         * ---------------------- */
        try {
            db.execSQL(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** DB에 데이터 추가 메소드
     * @param filter    추가할 데이터
     * @return          추가됐는지 여부 */
    boolean addFilter(String filter) {
        try {
            db.execSQL("INSERT INTO " + DB_NAME +
                    " VALUES(\'" + filter + "\');");
        }
        catch (SQLException e) {
            return false;
        }
        return true;
    }

    /** DB 데이터 삭제 메소드
     * @param filter    삭제할 데이터
     * @return          삭제됐는지 여부 */
    boolean removeFilter(String filter) {
        try {
            db.execSQL("DELETE FROM " + DB_NAME
                    + " WHERE filterName=\'" + filter + "\';");
        }
        catch (SQLException e) {
            return false;
        }
        return true;
    }

    /** DB 데이터를 가져오는 메소드
     * @param filters    데이터가 들어 갈 ArrayList. 포인터를 통해 데이터 제공 */
    public void getFilters(ArrayList<String> filters) {
        if (filters == null) return;

        Cursor cursor = db.rawQuery("SELECT * FROM " + DB_NAME, null);
        while (cursor.moveToNext()) {
            filters.add(cursor.getString(0));
        }
        cursor.close();
    }

    public void closeDB() {
        db.close();
    }
}

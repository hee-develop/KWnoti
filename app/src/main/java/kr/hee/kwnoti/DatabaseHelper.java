package kr.hee.kwnoti;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/** DB를 관리하는 클래스의 추상 클래스 */
public abstract class DatabaseHelper {
    private SQLiteDatabase db;
    private final String DB_NAME;

    /** 생성자, DB 생성 및 DB 이름을 넣어 줌
     * @param context    DatabaseHelper 클래스를 부른 액티비티
     * @param db_name    DB의 이름 */
    public DatabaseHelper(String db_name, Context context) {
        DB_NAME = db_name;
        createDB(context);
    }

    // DB 생성 및 반환 메소드
    protected final void setDB(SQLiteDatabase db) {
        this.db = db;
    }
    protected final SQLiteDatabase getDB() {
        return db;
    }

    /** DB 생성 ABSTRACT 메소드. setDB() 메소드 반드시 호출 필요. */
    public abstract void createDB(Context context);
    /** DB 데이터 추가 ABSTRACT 메소드. Object 타입 클래스는 형변환해서 사용 가능.
     * @return 추가가 제대로 됐는지 여부 */
    public abstract boolean addToDB(Object value);
    /** DB 데이터를 가져오는 ABSTRACT 메소드. */
    public abstract void getData(Object array);
    /** DB 데이터 삭제 메소드 */
    public final void cleanDB() {
        db.execSQL("DELETE FROM " + DB_NAME);
    }

    /** DB 연결 해제 메소드 */
    public final void closeDB() {
        if (db.isOpen()) db.close();
    }
}


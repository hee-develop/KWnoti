package kr.hee.kwnoti;

import android.app.Activity;
import android.content.Context;

import com.tsengvn.typekit.TypekitContextWrapper;

/** 모든 액티비티의 기본 */
public class ActivityBase extends Activity {
    /** 폰트 삽입 메소드 */
    @Override protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}

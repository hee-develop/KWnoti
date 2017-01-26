package kr.hee.kwnoti.settings_activity;

import android.app.Activity;
import android.os.Bundle;

import kr.hee.kwnoti.R;

/** 설정의 앱 정보 화면 */
public class AppInfoActivity extends Activity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_app_info);
    }
}

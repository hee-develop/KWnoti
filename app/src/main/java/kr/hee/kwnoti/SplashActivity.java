package kr.hee.kwnoti;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/** 스플래시 스크린. 실행 후 바로 메인으로 넘어 감 */
public class SplashActivity extends Activity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}

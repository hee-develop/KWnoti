package kr.hee.kwnoti.settings_activity;

import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import kr.hee.kwnoti.R;

/** 설정의 앱 정보 액티비티 */
public class AppInfoActivity extends AppCompatActivity {
    TextView openSource;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_app_info);

        openSource = (TextView)findViewById(R.id.appInfo_openSource);

        // 오픈소스 내용 출력
        Spanned htmlString;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            htmlString = Html.fromHtml(getString(R.string.app_openSource_sourceList));
        else
            htmlString = Html.fromHtml(getString(R.string.app_openSource_sourceList),
                    Html.FROM_HTML_MODE_LEGACY);
        openSource.setText(htmlString);

        // URL 클릭 시 이동 설정
        openSource.setClickable(true);
        openSource.setMovementMethod(LinkMovementMethod.getInstance());
    }
}

package kr.hee.kwnoti;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

/** 와이파이 아이디 생성 액티비티 */
public class WifiActivity extends Activity {
    TextView privacyView;
    CheckBox checkBox;
    EditText password;
    Button   requestBtn;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
    }

    /** 뷰 초기화 메소드 */
    void initView() {
        setContentView(R.layout.activity_wifi);

        privacyView = (TextView)findViewById(R.id.wifi_privacy);
        checkBox    = (CheckBox)findViewById(R.id.wifi_checkBox);
        password    = (EditText)findViewById(R.id.wifi_password);
        requestBtn  = (Button)findViewById(R.id.wifi_btn_request);

        // 개인정보 동의 텍스트 표시
        Spanned htmlString;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            htmlString = Html.fromHtml(getString(R.string.wifi_privacy));
        else
            htmlString = Html.fromHtml(getString(R.string.wifi_privacy),
                    Html.FROM_HTML_MODE_LEGACY);
        privacyView.setText(htmlString);


    }


    // 아이디 생성 버튼 리스너
    View.OnClickListener requestListener = new View.OnClickListener() {
        @Override public void onClick(View view) {
            // 동의했는지 여부 확인
            boolean privacyIsChecked = checkBox.isChecked();

            // 비밀번호를 제대로 입력했는지 확인
            boolean passwordIsOk     = checkPwd(password.getText().toString());

            // 동의하지 않았거나 제대로 입력하지 않았다면 요청하지 않음
            if (!privacyIsChecked) {
                UTILS.showToast(WifiActivity.this, "개인정보 수집에 동의하셔야 진행이 가능합니다.");
                return;
            }

            if (!passwordIsOk) {
                UTILS.showToast(WifiActivity.this, "비밀번호는 6~8자리 알파벳/숫자만 가능합니다.");
                return;
            }

            // 아이디 요청
            //TODO
        }
    };

    public boolean checkPwd(String pwd) {
        boolean passwordIdOk;

        // 글자 수가 6~8 자리인 경우에만 true 반환
        // 글자가 숫자와 영문이 모두 포함됐을 때만 true 반환
        String regex = "[^a-zA-Z0-9]+";
        passwordIdOk = Pattern.matches(regex, pwd)
                && (pwd.length() >= 6 && pwd.length() <= 8);

        return passwordIdOk;
    }
}

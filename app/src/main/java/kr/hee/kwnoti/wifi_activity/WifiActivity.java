package kr.hee.kwnoti.wifi_activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.regex.Pattern;

import kr.hee.kwnoti.R;
import kr.hee.kwnoti.UTILS;
import retrofit2.Retrofit;

/** 와이파이 아이디 생성 액티비티 */
public class WifiActivity extends Activity {
    TextView privacyView;
    CheckBox checkBox;
    EditText password;
    Button   requestBtn;

    /** 폰트 삽입 메소드 */
    @Override protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 뷰 초기화
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

        // 요청 버튼에 대한 리스너
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (userValueIsValid(password.getText().toString()))
                    requestId();
            }
        });

        // 비밀번호에서 확인 버튼을 눌렀을 때의 리스너
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override public boolean onEditorAction(TextView tv, int id, KeyEvent event) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    String pwd = tv.getText().toString();

                    if (userValueIsValid(pwd)) {
                        requestId();
                        return false;
                    }
                }
                return true;
            }
        });
    }

    void requestId() {
        Retrofit retrofit = new Retrofit.Builder().build();

    }

    /** 체크했는지, 올바른 비밀번호를 입력했는지 확인하는 메소드
     * @param pwd    비밀번호
     * @return       체크됐고, 비밀번호가 입력됐을 때 true */
    boolean userValueIsValid(String pwd) {
        // 동의했는지 여부 확인
        boolean privacyIsChecked = checkBox.isChecked();

        // 비밀번호를 제대로 입력했는지 확인
        boolean passwordIsOk     = checkPwd(pwd);

        // 동의하지 않았거나 제대로 입력하지 않았다면 요청하지 않음
        if (!privacyIsChecked) {
            UTILS.showToast(WifiActivity.this, "개인정보 수집에 동의하셔야 진행이 가능합니다.");
            return false;
        }

        if (!passwordIsOk) {
            UTILS.showToast(WifiActivity.this, "비밀번호는 6~8자리 알파벳/숫자만 가능합니다.");
            return false;
        }

        return true;
    }

    /** 비밀번호를 올바르게 넣었는지 확인하는 메소드
     * @param pwd    비밀번호
     * @return       올바른 비밀번호인 경우 true */
    public boolean checkPwd(String pwd) {
        boolean passwordIdOk;

        // 글자 수가 6~8 자리인 경우에만 true 반환
        // 글자가 숫자와 영문이 모두 포함됐을 때만 true 반환
        String regex = "[a-zA-Z0-9]+";
        passwordIdOk = Pattern.matches(regex, pwd)
                && (pwd.length() >= 6 && pwd.length() <= 8);

        return passwordIdOk;
    }
}

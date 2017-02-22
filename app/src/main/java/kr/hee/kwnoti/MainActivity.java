package kr.hee.kwnoti;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import kr.hee.kwnoti.calendar_activity.CalendarActivity;
import kr.hee.kwnoti.info_activity.InfoActivity;
import kr.hee.kwnoti.settings_activity.SettingsActivity;
import kr.hee.kwnoti.student_card_activity.StudentCardActivity;
import kr.hee.kwnoti.tel_activity.TelActivity;
import kr.hee.kwnoti.u_campus_activity.UCampusMainActivity;

public class MainActivity extends Activity implements View.OnClickListener {
    TextView    btn_card,       // 학생증
                btn_calendar,   // 학사일정
                btn_tel,        // 교내 전화번호
                btn_info,       // 학교 공지사항
                btn_uCampus,    // 유캠퍼스 공지사항
                btn_links,      // 바로가기 모음
                btn_settings;   // 설정
    boolean     weatherActive;  // 실시간 날씨 사용 여부

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkFirstUse();            // 최초 실행 여부 검사
        weatherActive = checkUseWeatherService(); // 날씨 서비스를 켰는지 여부 확인
        initView();                 // 뷰 초기화
    }

    /** 최초 실행인지 확인하는 메소드 */
    void checkFirstUse() {
        SharedPreferences firstUse = PreferenceManager.
                getDefaultSharedPreferences(MainActivity.this);
        if (firstUse.getBoolean(KEY.FIRST_USE, true)) {
            //startActivity(new Intent(this, FirstActivity.class)); TODO
            FirebaseMessaging.getInstance().subscribeToTopic("notice");
            //finish();

            // 최초 실행 종료
            SharedPreferences.Editor editor = firstUse.edit();
            editor.putBoolean(KEY.FIRST_USE, false).apply();
        }
    }

    /** 학교의 날씨를 받아올지 설정을 가져오는 메소드
     * @return  날씨 설정 여부 */
    boolean checkUseWeatherService() {
        SharedPreferences useWeather = PreferenceManager.
                getDefaultSharedPreferences(MainActivity.this);
        return useWeather.getBoolean(getString(R.string.key_weatherService), true);
    }

    /** 뷰 초기화 메소드 */
    void initView() {
        setContentView(R.layout.activity_main);

        btn_card    = (TextView)findViewById(R.id.main_btn_idCard);
        btn_calendar= (TextView)findViewById(R.id.main_btn_calendar);
        btn_tel     = (TextView)findViewById(R.id.main_btn_tel);
        btn_info    = (TextView)findViewById(R.id.main_btn_info);
        btn_uCampus = (TextView)findViewById(R.id.main_btn_uInfo);
        btn_links   = (TextView)findViewById(R.id.main_btn_links);
        btn_settings= (TextView)findViewById(R.id.main_btn_settings);

        btn_card.setOnClickListener(this);
        btn_calendar.setOnClickListener(this);
        btn_tel.setOnClickListener(this);
        btn_info.setOnClickListener(this);
        btn_uCampus.setOnClickListener(this);
        btn_links.setOnClickListener(this);
        btn_settings.setOnClickListener(this);

        //Document doc = Jsoup.connect("http://m.kma.go.kr/m/observation/observation_01.jsp").get();
    }

    @Override public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.main_btn_idCard :
                startActivity(new Intent(this, StudentCardActivity.class)); break;
            case R.id.main_btn_calendar :
                startActivity(new Intent(this, CalendarActivity.class)); break;
            case R.id.main_btn_tel :
                startActivity(new Intent(this, TelActivity.class)); break;
            case R.id.main_btn_info :
                startActivity(new Intent(this, InfoActivity.class)); break;
            case R.id.main_btn_uInfo :
                startActivity(new Intent(this, UCampusMainActivity.class)); break;
            case R.id.main_btn_links :
                final String[] name = {
                        "광운대 대신 전해드려요 페이스북",
                        "광운대학교 대나무숲 페이스북",
                        "총학생회 페이스북",
                        "중앙도서관",
                        "미디어광운",
                        "총동문회" };
                final String[] url = {
                        "https://www.facebook.com/kwtalk/",
                        "https://www.facebook.com/kwubamboo/",
                        "https://www.facebook.com/kw2017TheGreen/",
                        "http://kupis.kw.ac.kr/",
                        "http://www.mediakw.org/",
                        "http://dongmoon.kw.ac.kr/" };

                // 링크가 여러개 있으므로 다이얼로그로 띄워줌
                UTILS.showAlertDialog(MainActivity.this, "어디로 갈까요?", name,
                        new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dInterface, int i) {
                                Intent intent = new Intent(getApplicationContext(), BrowserActivity.class);
                                intent.putExtra(KEY.BROWSER_URL, url[i]);
                                startActivity(intent);
                            }
                        }
                );
                break;
            case R.id.main_btn_settings :
                startActivity(new Intent(this, SettingsActivity.class)); break;
        }
    }
}

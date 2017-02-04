package kr.hee.kwnoti.settings_activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import kr.hee.kwnoti.R;

/** 설정 액티비티. XML 파일을 불러 화면에 띄워줌 */
public class SettingsActivity extends Activity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.main_settings));
        setContentView(R.layout.activity_settings);
        getFragmentManager().beginTransaction().replace(
                R.id.setting_frame, new SettingsFragment()).commit();
    }

    /** 설정 화면을 위한 프래그먼트. settings.xml 데이터를 가져 옴 */
    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {
        EditTextPreference  studentId,
                            studentName,
                            studentMajor;
        String key_stuId, key_stuName, key_stuMajor;

        @Override public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            pref.registerOnSharedPreferenceChangeListener(this);

            initView();
            setData(pref);
        }

        /** Summary 값을 써 주기 위한 변수 선언 */
        void initView() {
            key_stuId   = getString(R.string.key_studentID);
            key_stuName = getString(R.string.key_studentName);
            key_stuMajor= getString(R.string.key_studentMajor);

            studentId   = (EditTextPreference)findPreference(key_stuId);
            studentName = (EditTextPreference)findPreference(key_stuName);
            studentMajor= (EditTextPreference)findPreference(key_stuMajor);
        }

        /** Summary 값 변경 */
        void setData(SharedPreferences pref) {
            studentId.setSummary(pref.getString(key_stuId, ""));
            studentName.setSummary(pref.getString(key_stuName, ""));
            studentMajor.setSummary(pref.getString(key_stuMajor, ""));
        }

        @Override public void onPause() {
            super.onPause();
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            pref.unregisterOnSharedPreferenceChangeListener(this);
        }

        /** onSharedPreferenceChanged
         * 설정에서 값이 변경됐을 때 호출
         * @param pref  설정 SharedPreference
         * @param key   호출된 key 값 */
        @Override public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
            // 푸쉬 알림 켬/끔
            if (key.equals(getString(R.string.key_pushActive))) {
                final FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
                if (pref.getBoolean(key, true)) firebaseMessaging.subscribeToTopic("notice");
                else                            firebaseMessaging.unsubscribeFromTopic("notice");
            }
            else setData(pref);
        }
    }
}
// 출처 http://stackoverflow.com/questions/3326317/possible-to-autocomplete-a-edittextpreference/4309195
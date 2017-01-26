package kr.hee.kwnoti.settings_activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

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
        @Override public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            pref.registerOnSharedPreferenceChangeListener(this);
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
            if (key.equals(getString(R.string.key_pushActive))) {
                // 푸쉬 알림을 받는다고 하는 경우
                if (pref.getBoolean(key, true))
                    FirebaseMessaging.getInstance().subscribeToTopic("notice");
                else
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("notice");
            }
        }
    }
}

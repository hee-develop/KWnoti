package kr.hee.kwnoti;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/** 푸쉬 알람을 위한 FCM(Firebase Cloud Messaging) 서비스
 * 이 서비스는 추후 활용될 수 있는 디바이스의 토큰을 따로 저장해둔다 */
public class FcmIdService extends FirebaseInstanceIdService {
    @Override public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();

        if (token == null) return;

        // 추후 활용될 수 있으므로, 토큰 값을 따로 저장해 둔다.
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY.PREFERENCE_DEVICE_TOKEN, token).apply();
    }
}

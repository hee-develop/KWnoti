package kr.hee.kwnoti;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

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

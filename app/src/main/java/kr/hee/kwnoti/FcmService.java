package kr.hee.kwnoti;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.hee.kwnoti.settings_activity.PushFilterDB;

/** 파이어베이스에서 오는 푸쉬 알람을 받는 서비스 */
public class FcmService extends FirebaseMessagingService {
    @Override public void onMessageReceived(RemoteMessage remoteMessage) {
        JSONObject jsonObject = new JSONObject();
        String jsonString;
        try {
            Log.d("Firebase alarm", ""+remoteMessage.getData());

            // 파이어베이스 JSON 데이터에서 message 단어를 제외시킨 뒤, 다시 JSON 모양으로 포장
            jsonObject.put("data", remoteMessage.getData());
            jsonString = jsonObject.getString("data").replace("{message=", "");
            jsonObject = new JSONObject(jsonString);

            // 만약 사용자가 알림을 받지 않기로 한 문구가 포함되어 있다면, 알림을 울리지 않음 -------------------
            PushFilterDB db = new PushFilterDB(getApplicationContext());
            ArrayList<String> filters = new ArrayList<>();
            db.getFilters(filters);
            for (int i = 0; i < filters.size(); i++) {
                // 제목에 포함되어 있거나
                if (jsonObject.getString("title").contains(filters.get(i))) return;
                    // 작성자에 포함되어 있거나
                else if (jsonObject.getString("content").contains(filters.get(i))) return;
            }
            // 알람 기타 설정(진동 등) ----------------------------------------------------------------
            SharedPreferences prefs = PreferenceManager.
                    getDefaultSharedPreferences(getApplicationContext());
            boolean vibratorActive = prefs.getBoolean(getString(R.string.key_pushVibrator), true);
            boolean soundActive = prefs.getBoolean(getString(R.string.key_pushSound), true);
            // -------------------------------------------------------------------------------------

            Intent notiIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.getString("link")));
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, notiIntent, PendingIntent.FLAG_ONE_SHOT);
            // 알람 선언 및 표시
            NotificationManager notiManager = (NotificationManager)getSystemService(
                    NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(this)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setSmallIcon(R.drawable.splash_mark)
                    .setDefaults(
                            (soundActive ? Notification.DEFAULT_SOUND : 0) |
                            (vibratorActive ? Notification.DEFAULT_VIBRATE : 0))
                    .setLights(Color.argb(255, 255, 60, 100), 600, 6000)
                    .setContentTitle(jsonObject.getString("title"))
                    .setContentText(jsonObject.getString("content"))
                    .setTicker(jsonObject.getString("title"))
                    .setContentIntent(pIntent)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true);
            notiManager.notify(0, builder.build());
            // TODO Intent 커스텀 브라우저로 수정 & 소리, 진동 등 재설정
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

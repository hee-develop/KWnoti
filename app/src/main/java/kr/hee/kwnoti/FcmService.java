package kr.hee.kwnoti;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
    private static final String TAG = "FCM Alarm";

    @Override public void onMessageReceived(RemoteMessage remoteMessage) {
        JSONObject jsonObject = new JSONObject();
        String jsonString;
        try {
            Log.d(TAG, ""+remoteMessage.getData());

            // 파이어베이스 JSON 데이터에서 message 단어를 제외시킨 뒤, 다시 JSON 모양으로 포장
            jsonObject.put("data", remoteMessage.getData());
            jsonString = jsonObject.getString("data").replace("{message=", "");
            jsonObject = new JSONObject(jsonString);

            String  title   = jsonObject.getString("title"),
                    content = jsonObject.getString("content"),
                    url     = jsonObject.getString("link");
            Context context = getApplicationContext();

            // 만약 사용자가 알림을 받지 않기로 한 문구가 포함되어 있다면, 알림을 울리지 않음 -------------------
            PushFilterDB db = new PushFilterDB(context);
            ArrayList<String> filters = new ArrayList<>();
            db.getFilters(filters);
            for (int i = 0; i < filters.size(); i++) {
                // 제목에 포함되어 있거나
                if (title.contains(filters.get(i))) return;
                // 작성자에 포함되어 있거나
                else if (content.contains(filters.get(i))) return;
            }
            // 알람 기타 설정(진동 등) ----------------------------------------------------------------
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean vibratorActive = prefs.getBoolean(getString(R.string.key_pushVibrator), true);
            boolean soundActive = prefs.getBoolean(getString(R.string.key_pushSound), true);
            // -------------------------------------------------------------------------------------

            // 클릭했을 때 이동할 액티비티 설정
            Intent intent = new Intent(context, BrowserActivity.class);
            intent.putExtra(KEY.BROWSER_URL, url);
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

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
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

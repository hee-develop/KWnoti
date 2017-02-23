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
        try {
            // 받은 메세지 로그 출력
            Log.d(TAG, ""+remoteMessage.getData());

            // JSON 모양 변경 (불필요한 message 껍데기는 제거)
            jsonObject.put("data", remoteMessage.getData());
            String jsonString = jsonObject.getString("data").replace("{message=", "");
            jsonObject = new JSONObject(jsonString);

            Context context = getApplicationContext();
            String  title   = jsonObject.getString("title"),
                    whoWrite= jsonObject.getString("content"),
                    url     = jsonObject.getString("link");
            //int requestCode = Integer.parseInt(url.split("&|=")[3]);

            // 필터링 할 단어가 있으면 알림을 울리지 않게 함
            if (!this.isFiltered(context, title, whoWrite))
                return;

            // 알람 기타 설정(진동 등)
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean vibratorActive = prefs.getBoolean(getString(R.string.key_pushVibrator), true);
            boolean soundActive = prefs.getBoolean(getString(R.string.key_pushSound), true);

            // 클릭했을 때 이동할 액티비티 설정
            Intent intent = new Intent(context, BrowserActivity.class);
            intent.putExtra(KEY.BROWSER_URL, url);
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            // 알람 선언 및 표시
            NotificationManager notiManager = (NotificationManager)getSystemService(
                    NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(this)
                    .setContentTitle(title)         // 제목
                    .setContentText(whoWrite)       // 작성자
                    .setTicker("새 알림이 있습니다.")  // 제목(소형 알람이 울리게 설정돼있을 경우)
                    .setContentIntent(pIntent)      // 클릭 시 실행될 인텐트
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setSmallIcon(R.drawable.splash_mark)
                    .setDefaults(
                            (soundActive ? Notification.DEFAULT_SOUND : 0) |
                            (vibratorActive ? Notification.DEFAULT_VIBRATE : 0))
                    .setLights(Color.argb(255, 255, 60, 100), 600, 6000)
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true);
            notiManager.notify(0, builder.build());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /** 필터 DB에 걸리는 내용이 포함되어 있는지 확인하는 메소드
     * @param title       알람의 제목
     * @param whoWrite    알람의 작성자
     * @return            울리게 할지 여부 */
    boolean isFiltered(Context context, String title, String whoWrite) {
        // 필터 데이터 불러오기
        PushFilterDB db = new PushFilterDB(context);
        ArrayList<String> filters = new ArrayList<>();
        db.getFilters(filters);

        // 제목이나 작성자에 필터링 할 데이터가 존재하는지 확인
        int size = filters.size();
        for (int i = 0; i < size; i++) {
            String filter = filters.get(i);
            if (title.contains(filter)) return false;
            else if (whoWrite.contains(filter)) return false;
        }
        return true;
    }
}

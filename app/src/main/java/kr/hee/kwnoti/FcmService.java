package kr.hee.kwnoti;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
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
    private static final String GROUP_NAME = "KwAlarm";
    private static final int GROUP_ID = 954;

    @Override public void onMessageReceived(RemoteMessage remoteMessage) {
        // 알림을 수신하면 로그로 출력
        Log.d(TAG, ""+remoteMessage.getData());

        Context context = getApplicationContext();

        // 알람에 들어갈 데이터들(제목, 작성자, URL, 알림 고유번호)
        String  title, whoWrite, url;
        int reqCode;

        // {message ...} 껍데기 제거
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", remoteMessage.getData());
            String jsonString = jsonObject.getString("data").replace("{message=", "");
            jsonObject = new JSONObject(jsonString);
            // JSON 데이터 추출
            title   = jsonObject.getString("title");
            whoWrite= jsonObject.getString("content");
            url     = jsonObject.getString("link");
            // 광운대에서 오지 않은 메세지에 대한 예외처리
            if (url.contains("kw.ac.kr"))
                reqCode = Integer.parseInt(url.split("&|=")[3]);
            else
                reqCode = 0;
        }
        // 제대로 자르는데에 실패하면 알람을 울리지 않음
        catch (JSONException e) {
            UTILS.showToast(context, "오류 : 잘못된 알림 도착");
            e.printStackTrace();
            return;
        }

        // 사용자 지정 단어 필터링
        if (!this.isFiltered(context, title, whoWrite)) return;

        // 알림과 관련된 진동, 알림음 설정 (기본값 : 참)
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean vibratorActive = prefs.getBoolean(getString(R.string.key_pushVibrator), true);
        boolean soundActive = prefs.getBoolean(getString(R.string.key_pushSound), true);

        // 클릭했을 때 이동할 액티비티 설정
        // 앱에서 여러 개의 알람을 울리게 할 수 있도록 FLAG_ONE_SHOT & requestCode 설정
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra(KEY.BROWSER_URL, url);
        PendingIntent pIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_ONE_SHOT);

        // 알람 선언
        NotificationManager notiManager = (NotificationManager)getSystemService(
                NOTIFICATION_SERVICE);

        // 안드로이드 8.0 이상에서 동작하는 알림 그룹 설정
        NotificationChannel notiChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String id = "Notice";
            int importance = NotificationManager.IMPORTANCE_LOW;
            notiChannel = new NotificationChannel(id, getString(R.string.app_name), importance);
            notiManager.createNotificationChannel(notiChannel);
        }

        // 누가(안드로이드 7.0) 이상에서만 동작하는 알람 그룹 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Notification.Builder groupBuilder = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setColor(getColor(R.color.brown500))
                    .setSmallIcon(R.drawable.splash_mark)
                    .setAutoCancel(true)
                    .setSubText(getString(R.string.noti_unread))
                    .setGroup(GROUP_NAME)
                    .setGroupSummary(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                groupBuilder.setChannelId("Notice");
            }
            notiManager.notify(TAG, GROUP_ID, groupBuilder.build());
        }

        // 알람 설정
        Notification.Builder builder = new Notification.Builder(this)
                // 제목(ContentTitle) 및 작성자(ContentText), 시간 설정
                .setContentTitle(title)
                .setContentText(whoWrite)
                .setWhen(System.currentTimeMillis())
                // 그룹 설정
                .setGroup(GROUP_NAME)
                // 아이콘 설정
                .setSmallIcon(R.drawable.splash_mark)
                // 클릭 시 이동할 인텐트 설정
                .setContentIntent(pIntent)
                // 진동, 소리, LED 설정
                .setDefaults((soundActive   ? Notification.DEFAULT_SOUND : 0) |
                            (vibratorActive ? Notification.DEFAULT_VIBRATE : 0))
                .setLights(Color.argb(255, 255, 60, 100), 600, 6000)
                // 클릭 시 사라지도록 설정
                .setAutoCancel(true);
        notiManager.notify(reqCode, builder.build());
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

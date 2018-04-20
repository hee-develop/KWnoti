package kr.hee.kwnoti

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kr.hee.kwnoti.settings_activity.PushFilterDB
import java.util.*

val TOKEN_KEY = "FCM Device Token"
val CHANNEL_ID = "Notice"
private val TAG = "FCM Push Service"

/* 파이어베이스 토큰 저장 서비스 */
class FcmIdService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        val token = FirebaseInstanceId.getInstance().token ?: return

        // SharedPreferences 내부에 토큰 값 저장
        // 현재는 사용하지 않는 토큰 값임
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
                .edit().putString(TOKEN_KEY, token).apply()
    }
}

/* 파이어베이스 푸시 수신 서비스 */
class FcmService : FirebaseMessagingService() {
    private val GROUP_ID    = 954
    private val GROUP_NAME  = "KW Alarm"

    override fun onMessageReceived(remoteMsg: RemoteMessage?) {
        if (remoteMsg == null) return

        // 로그 출력
        Log.d(TAG, remoteMsg.toString())

        // 메세지 추출
        val message : Map<String, String> = remoteMsg.data

        // 알림 데이터
        val title : String?  = message.get("title")     // 제목
        val writer : String? = message.get("content")   // 작성자
        val url : String?    = message.get("link")      // URL
        val reqCode : Int    = if (url == null)
            0
        else
            Integer.parseInt(url.split("&|=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[3])
        // 알림 인텐트
        val intent = Intent(applicationContext, BrowserActivity::class.java)
        intent.putExtra(KEY.BROWSER_URL, url)
        val pIntent = getActivity(applicationContext, reqCode, intent, FLAG_ONE_SHOT)
        // TODO 키값 변경 필요

        // 사용자가 필터링 설정한 단어가 포함되면 울리지 않음
        if (!this.isFiltered(applicationContext, title, writer)) return

        // 알림 생성
        val notiManager : NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notiBuilder : Notification.Builder = setNotiBuilder()
        setNotiBuilderValue(notiBuilder, title, writer, pIntent)

        // 알림 표시
        notiManager.notify(TAG, reqCode, notiBuilder.build())
    }

    /** 필터 DB에 걸리는 내용이 포함되어 있는지 확인하는 메소드
     * @param title       알람의 제목
     * @param writer      알람의 작성자
     * @return            울리게 할지 여부
     */
    private fun isFiltered(context: Context, title: String?, writer: String?): Boolean {
        if (title == null || writer == null) return false

        // 필터 데이터 불러오기
        val db = PushFilterDB(context)
        val filters = ArrayList<String>()
        db.getFilters(filters)

        // 제목이나 작성자에 필터링 할 데이터가 존재하는지 확인
        val size = filters.size
        for (i in 0 until size) {
            val filter = filters[i]
            if (title.contains(filter))
                return false
            else
                if (writer.contains(filter))
                    return false
        }
        return true
    }

    /** 빌더 생성 메소드 */
    private fun setNotiBuilder() : Notification.Builder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            Notification.Builder(this, CHANNEL_ID)
        else
            Notification.Builder(this)
    }

    /** 알림 값 삽입 메소드
     * @param builder   빌더
     * @param title     제목
     * @param writer    작성부서
     * @param pIntent   터치 시 열릴 인텐트 */
    private fun setNotiBuilderValue(
            builder : Notification.Builder,
            title  : String?,
            writer : String?,
            pIntent: PendingIntent) {
        builder.setContentTitle(title)
                .setContentText(writer)
                .setWhen(System.currentTimeMillis())
                .setGroup(GROUP_NAME)
                .setSmallIcon(R.drawable.splash_mark)
                .setAutoCancel(true)
                .setContentIntent(pIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID)
        }
    }
}

/** FCM 채널 설정 메소드 */
fun setFcmChannel(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    // 알림 진동, 소리
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val isLightActive = prefs.getBoolean(context.getString(R.string.key_pushLight), true)
    val isSoundActive = prefs.getBoolean(context.getString(R.string.key_pushSound), true)
    val isVibrateActive = prefs.getBoolean(context.getString(R.string.key_pushVibrator), true)

    // 채널 옵션 설정
    val channel = NotificationChannel(CHANNEL_ID, "KW Notice", IMPORTANCE_LOW)
    channel.description = "KW all"
    channel.enableLights(isLightActive)
    channel.lightColor = Color.argb(255, 255, 60, 100)
    if (!isSoundActive) channel.setSound(null, null)
    channel.enableVibration(isVibrateActive)

    // 채널 등록
    val notiManager : NotificationManager? =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notiManager?.createNotificationChannel(channel)
}
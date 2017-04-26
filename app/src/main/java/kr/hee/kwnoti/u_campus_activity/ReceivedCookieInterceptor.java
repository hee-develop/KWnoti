package kr.hee.kwnoti.u_campus_activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import kr.hee.kwnoti.KEY;
import okhttp3.Interceptor;
import okhttp3.Response;

/** Set-Cookie 요청을 받아 쿠키를 저장하게 하는 인터셉터 */
public class ReceivedCookieInterceptor implements Interceptor {
    private Context context;
    public ReceivedCookieInterceptor(Context context) {
        this.context = context;
    }

    @Override public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        // 응답에서 쿠키 추출
        List<String> headers = response.headers("Set-Cookie");

        // 헤더에 문제가 있는 경우 바로 보내줌
        if (headers == null || headers.isEmpty())
            return response;

        // 기존 쿠키 불러오기
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        HashSet<String> cookies = (HashSet<String>)prefs.getStringSet(KEY.COOKIE_SET, new HashSet<String>());

        // 쿠키를 여러번 받는데, 첫 번째 쿠키는 7개를 받지만 나머지는 2개씩 받음. 이런 쿠키들은 기존 쿠키에 추가
        // 7개를 받으면 새로 받는다는 것을 의미하므로 초기화
        if (cookies.size() <= headers.size())
            cookies = new HashSet<>();

        // 쿠키에서 불필요한 부분들은 빼고 저장
        for (String header : headers) {
            String[] head = header.split("Expires=|Path=|path=");
            cookies.add(head[0]);
        }

        // 쿠키 저장
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putStringSet(KEY.COOKIE_SET, cookies).apply();

        return response;
    }
}
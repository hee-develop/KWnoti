package kr.hee.kwnoti.u_campus_activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.HashSet;

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
        // 헤더에 문제가 있는 경우 바로 보내줌
        if (response.header("Set-Cookie") == null || response.header("Set-Cookie").isEmpty())
            return response;

        // 쿠키를 저장할 Set
        HashSet<String> cookies = new HashSet<>();
        for (String header : response.headers("Set-Cookie")) {
            String[] headers = header.split("; "); // 불필요한 뒷부분은 제거
            cookies.add(headers[0]);
        }
        // 쿠키 저장
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putStringSet(KEY.COOKIE_SET, cookies).apply();

        return response;
    }
}
package kr.hee.kwnoti.u_campus_activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import kr.hee.kwnoti.KEY;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/** 쿠키를 추가해 요청을 하게 하는 인터셉터 */
class AddCookieInterceptor implements Interceptor {
    private Context context;

    AddCookieInterceptor(Context context) {
        this.context = context;
    }

    /** 쿠키가 있는지 여부를 반환 */
    public boolean hasCookie() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> cookies = pref.getStringSet(KEY.COOKIE_SET, null);
        return !(cookies == null || cookies.isEmpty());
    }

    @Override public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        // 쿠키값(Preference 내에 저장)을 불러 와 헤더에 추가
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> cookies = pref.getStringSet(KEY.COOKIE_SET, null);
        // 예외처리
        if (cookies != null)
            for (String cookie : cookies)
                builder.addHeader("Cookie", cookie);

        // 돌려줌
        return chain.proceed(builder.build());
    }
}

/** Set-Cookie 요청을 받아 쿠키를 저장하게 하는 인터셉터 */
class ReceivedCookieInterceptor implements Interceptor {
    private Context context;
    ReceivedCookieInterceptor(Context context) {
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
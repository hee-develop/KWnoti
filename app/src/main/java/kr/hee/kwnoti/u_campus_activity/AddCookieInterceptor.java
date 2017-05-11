package kr.hee.kwnoti.u_campus_activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.Set;

import kr.hee.kwnoti.KEY;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/** 쿠키를 추가해 요청을 하게 하는 인터셉터 */
public class AddCookieInterceptor implements Interceptor {
    private Context context;

    public AddCookieInterceptor(Context context) {
        this.context = context;
    }

    @Override public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        // 쿠키값(Preference 내에 저장)을 불러 와 헤더에 추가
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> cookies = pref.getStringSet(KEY.COOKIE_SET, null);

        // 쿠키 추가 및 예외처리
        if (cookies != null) {
            String reqCookie = "";
            for (String cookie : cookies) reqCookie += cookie + "; ";
            builder.header("Cookie", reqCookie);
        }
        // 돌려줌
        return chain.proceed(builder.build());
    }
}

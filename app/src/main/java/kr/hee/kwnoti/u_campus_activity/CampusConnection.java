package kr.hee.kwnoti.u_campus_activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kr.hee.kwnoti.KEY;
import kr.hee.kwnoti.SharedPreferencesHandler;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Singleton
 */
public class CampusConnection {
    private static final CampusConnection connInstance = new CampusConnection();
    public static CampusConnection getInstance() {
        return connInstance;
    }

    private Retrofit retrofit;

    private CampusConnection() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new RequestInterceptor()) // for request
                .addInterceptor(new ResponseInterceptor()) // for response
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://info.kw.ac.kr/")
                .client(client)
                .build();
    }

    public void getCampusLoginResponse(String stuId, String stuPwd, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = retrofit.create(CampusConnectionInterface.class).loginToCampus(
                "2", "https://info.kw.ac.kr/", "11",
                "N", "", stuId, stuPwd);
        call.enqueue(callback);
    }

    public void getCampusMain(Callback<ResponseBody> callback) {
        Call<ResponseBody> call = retrofit.create(CampusConnectionInterface.class).loadCampus();
        Call<ResponseBody> call2= retrofit.create(CampusConnectionInterface.class).loadStudentData();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                call2.enqueue(callback);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // todo
            }
        });
    }

    /**
     * Interceptor for request
     */
    private class RequestInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();

            // load cookies in shared-preferences and set header
            SharedPreferences pref = SharedPreferencesHandler.getSharedPreferences();
            Set<String> cookies = pref.getStringSet("COOKIE_SET", null);

            if (cookies == null) return chain.proceed(chain.request());

            StringBuilder reqCookie = new StringBuilder();
            for (String cookie : cookies)
                reqCookie.append(cookie).append("; ");
            builder.header("Cookie", reqCookie.toString());

            return chain.proceed(builder.build());
        }
    }

    /**
     * Interceptor for response
     */
    private class ResponseInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());

            List<String> headers = response.headers("Set-Cookie");

            if (headers.isEmpty()) return response;

            // load cookies
            Set<String> originalCookies = SharedPreferencesHandler.getSharedPreferences()
                    .getStringSet("COOKIE_SET", new HashSet<>());

            if (originalCookies.size() <= headers.size())
                originalCookies = new HashSet<>();

            // add new cookies in old cookies
            for (String header : headers) {
                String[] head = header.split("Expires=|Path=|path=");
                originalCookies.add(head[0]);
            }

            SharedPreferences.Editor editor = SharedPreferencesHandler.getSharedPreferencesEditor();
            editor.putStringSet("COOKIE_SET", originalCookies).apply();

            return response;
        }
    }
}

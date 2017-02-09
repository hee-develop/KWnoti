package kr.hee.kwnoti.u_campus_activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.io.IOException;

import kr.hee.kwnoti.R;
import kr.hee.kwnoti.UTILS;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UCampusActivity extends Activity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 쿠키를 불러 옴
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String  id = pref.getString(getString(R.string.key_studentID), ""),
                pwd = pref.getString(getString(R.string.key_studentUCampusPassword), "");

        // 유캠퍼스 로그인을 위한 쿠키(반응과 요청에 대한 인터셉터)
        AddCookieInterceptor cookieAdder = new AddCookieInterceptor(this);
        ReceivedCookieInterceptor cookieInterceptor = new ReceivedCookieInterceptor(this);
        // OkHttpClient 설정(쿠키 인터셉터들 삽입)
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(cookieAdder).addInterceptor(cookieInterceptor).build();
        // Retrofit 빌드
        Retrofit retrofit = new Retrofit.Builder().client(client).baseUrl(UCampusInterface.URL).build();
        UCampusInterface request = retrofit.create(UCampusInterface.class);
        Call<ResponseBody> response = request.getCookie("2", "http%3A%2F%2Finfo.kw.ac.kr%2F", "11", "", "KOREAN", id, pwd);

        // 로그인 요청
        response.enqueue(new Callback<ResponseBody>() {
            @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> res) {
                try {
                    // 정보를 잘못 입력했거나 정보가 없는 경우 로그인 실패로 패스
                    String responseHtml = new String(res.body().bytes(), "EUC-kR");
                    if (responseHtml.contains("비밀번호가 맞지 않습니다") || responseHtml.contains("비밀번호를 입력하세요"))
                        throw new IOException();
                }
                catch (IOException e) {
                    onFailure(call, e);
                }

            }

            @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
                UTILS.showToast(getApplicationContext(), "로그인에 실패했습니다.");
                t.printStackTrace();
            }
        });

        // 유캠퍼스 데이터 요청
        response = request.getMain();
        response.enqueue(new Callback<ResponseBody>() {
            @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> res) {
                try {
                    String frdspaj = res.body().string();
                    String a = "A";
                } catch (IOException e) {
                    e.printStackTrace();
                }
                res.raw();
            }

            @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}

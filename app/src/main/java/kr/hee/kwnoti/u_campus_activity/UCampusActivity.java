package kr.hee.kwnoti.u_campus_activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import kr.hee.kwnoti.R;
import kr.hee.kwnoti.UTILS;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UCampusActivity extends Activity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 쿠키를 불러 옴
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String  id = pref.getString(getString(R.string.key_studentID), ""),
                pwd = pref.getString(getString(R.string.key_studentUCampusPassword), "");

        // 로그인 요청
        new LoginThread(this, id, pwd).start();

        SetView setView = new SetView() {
            @Override
            public void setViewData() {

            }
        };
    }

    interface SetView {
        void setViewData();
    }

    class LoginThread extends Thread {
        OkHttpClient client;
        String id, pwd;

        LoginThread(Context context, String id, String password) {
            // 유캠퍼스 로그인을 위한 쿠키(반응과 요청에 대한 인터셉터)
            AddCookieInterceptor cookieAdder = new AddCookieInterceptor(context);
            ReceivedCookieInterceptor cookieInterceptor = new ReceivedCookieInterceptor(context);
            // OkHttpClient 설정(쿠키 인터셉터들 삽입)
            client = new OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS)
                    .addNetworkInterceptor(cookieAdder).addInterceptor(cookieInterceptor).build();
            this.id = id;
            this.pwd = password;
        }

        @Override public void run() {
            // Retrofit 빌드
            Retrofit retrofit = new Retrofit.Builder().client(client).baseUrl(UCampusInterface.LOGIN_URL).build();
            UCampusInterface request = retrofit.create(UCampusInterface.class);

            // 로그인 요청 ===========================================================================
            Call<ResponseBody> call = request.getCookie("2", "http%3A%2F%2Finfo.kw.ac.kr%2F", "11",
                    "", "KOREAN", id, pwd);
            try {
                Response<ResponseBody> response = call.execute();
                String responseHtml = new String(response.body().bytes(), "EUC-kR");
                if (responseHtml.contains("비밀번호가 맞지 않습니다") || responseHtml.contains("비밀번호를 입력하세요"))
                    throw new IOException();
            }
            catch (IOException e) {
                UTILS.showToast(getApplicationContext(), "로그인에 실패했습니다.");
                e.printStackTrace();
            }

            // 유캠퍼스 로드 요청 =====================================================================
            call = request.getUcampus();
            try {
                call.execute();
            }
            catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        UTILS.showToast(getApplicationContext(), "오류가 발생했습니다.");
                    }
                });
                e.printStackTrace();
            }

            // 유캠퍼스 데이터 로드 요청 ===============================================================
            call = request.getUcampusCore(/*"", "univ", "N000003", "U2016209697220013", "2016", "2", "01", "2014722028UA", "", "11", ""*/);
            try {
                Response<ResponseBody> response = call.execute();
                String responseHtml = response.body().string();
                setViewData(responseHtml);
            }
            catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        UTILS.showToast(getApplicationContext(), "오류가 발생했습니다.");
                    }
                });
                e.printStackTrace();
            }
        }

        void setViewData(String html) {
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select("td.list_txt");
            for (Element element : elements) {
                boolean a = element.isBlock();
            }
        }
    }
}

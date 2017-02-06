package kr.hee.kwnoti.u_campus_activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.net.HttpURLConnection;

import kr.hee.kwnoti.R;
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
        Retrofit retrofit;
        // 쿠키 데이터가 없는 경우
//        if (!cookieAdder.hasCookie()) {
            // TODO 쿠키와 내 학번이 같은지 확인
            retrofit = new Retrofit.Builder().client(client).baseUrl(FirstLoginInterface.URL).build();
            FirstLoginInterface request = retrofit.create(FirstLoginInterface.class);
            Call<ResponseBody> response = request.getCookie("2", "http%3A%2F%2Finfo.kw.ac.kr%2F",
                    "11", "", "KOREAN", "2014722028", "5813");

            response.enqueue(new Callback<ResponseBody>() {
                @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> res) {
                    res.headers();

                    Retrofit r = new Retrofit.Builder().baseUrl("http://info2.kw.ac.kr/servlet/controller.homepage.KwuMainServlet/").build();
                    UCampusInterface req = r.create(UCampusInterface.class);
                    Call<ResponseBody> res1 = req.getMain();
                    res1.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            response.code();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }

                @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });
//        }
        /*else
            retrofit = new Retrofit.Builder().client(client).baseUrl("").build(); // TODO 학번이 다를 떄 초기화해줘야지..*/




    }


    private static boolean findCookie(HttpURLConnection paramHttpURLConnection)
    {
        int i = 1;
        new StringBuilder();
        for (int j = i;; j++)
        {
            String str = paramHttpURLConnection.getHeaderFieldKey(j);
            if (str != null)
            {
                if (!str.equals("Set-Cookie")) {
                    continue;
                }
                if (paramHttpURLConnection.getHeaderField(j).contains("deleted")) {
                    i = 0;
                }
            }
            return i != 0;
        }
    }
}

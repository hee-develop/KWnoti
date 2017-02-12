package kr.hee.kwnoti;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import kr.hee.kwnoti.u_campus_activity.AddCookieInterceptor;
import kr.hee.kwnoti.u_campus_activity.Interface;
import kr.hee.kwnoti.u_campus_activity.ReceivedCookieInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BrowserActivity extends Activity {
    WebView webView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        webView = (WebView)findViewById(R.id.browser_webView);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        // 넘어온 인텐트 데이터 분석. 유캠퍼스에서 넘어온 정적 HTML 파일이면 다르게 불러야함
        Bundle intentData = getIntent().getExtras();
        boolean startFromUcampus = intentData.getBoolean(KEY.BROWSER_FROM_UCAMPUS, false);
        String title = intentData.getString(KEY.BROWSER_TITLE);
        setTitle(title);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (startFromUcampus) {
            // 유캠퍼스 로그인을 위한 쿠키(반응과 요청에 대한 인터셉터)
            AddCookieInterceptor cookieAdder = new AddCookieInterceptor(this);
            ReceivedCookieInterceptor cookieInterceptor = new ReceivedCookieInterceptor(this);
            // OkHttpClient 설정(쿠키 인터셉터들 삽입)
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS)
                    .addNetworkInterceptor(cookieAdder).addInterceptor(cookieInterceptor).build();
            // Retrofit 빌드
            Retrofit retrofit = new Retrofit.Builder().client(client).baseUrl(Interface.LOGIN_URL).build();
            Interface request = retrofit.create(Interface.class);


            String url = intentData.getString(KEY.BROWSER_URL);
            String bdSeq = intentData.getString(KEY.BROWSER_DATA);

            Call<ResponseBody> call = request.getContent(url, "view", bdSeq);
            call.enqueue(new Callback<ResponseBody>() {
                @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> res) {
                    try {
                        String body = res.body().string();
                        Document doc = Jsoup.parse(body);
                        Elements elements = doc.select("td.tl_l2");

                        webView.loadData(elements.html(), "text/html; charset=UTF-8", null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            UTILS.showToast(getApplicationContext(), "로드 실패");
                        }
                    });
                }
            });
        }
        else
            webView.loadUrl(intentData.getString(KEY.BROWSER_URL));
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}

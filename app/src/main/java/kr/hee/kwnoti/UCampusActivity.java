package kr.hee.kwnoti;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/*import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;*/

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;

//import cz.msebera.android.httpclient.Header;

public class UCampusActivity extends Activity {
//    // loopj Async http client & 쿠키
//    AsyncHttpClient  client = new AsyncHttpClient();
//    PersistentCookieStore cookie;
//    RequestParams    param = new RequestParams();
//
//    @Override protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_ucampus);
//
//        cookie = new PersistentCookieStore(this);
//
//        // 리퀘스트 헤더 추가
//        client.addHeader("Host", "info.kw.ac.kr");
//        client.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:52.0) Gecko/20100101 Firefox/52.0");
//        client.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//        client.addHeader("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.5,en;q=0.3");
//        client.addHeader("Accept-Encoding", "gzip, deflate, br");
//        client.addHeader("Referer", "https://info.kw.ac.kr/webnote/login/login2.php?loginOpt=&layout_opt=&js_domain=&redirect_url=http%3A%2F%2Finfo.kw.ac.kr%2F");
//        client.addHeader("Cookie", "languageName=KOREAN; ip_test=59.31.188.30");
//        client.addHeader("Connection", "keep-alive");
//        client.addHeader("Upgrade-Insecure-Requests", "1");
//        // POST 데이터 추가
//        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
//        param.put("login_type", "2");
//        param.put("redirect_url", "http%3A%2F%2Finfo.kw.ac.kr%2F");
//        param.put("layout_opt", "");
//        param.put("gubun_code", "11");
//        param.put("member_no", "2014722028");
//        param.put("password", "5813");
//        param.put("p_language", "KOREAN");
//        param.put("image.x", "0");
//        param.put("image.y", "0");
//
//        client.post("https://info.kw.ac.kr/webnote/login/login_proc.php", param, new AsyncHttpResponseHandler() {
//            @Override public void onSuccess(int resCode, Header[] headers, byte[] bytes) {
//                try {
//                    String html = new String(bytes, "EUC-KR");
//                    Document doc = Jsoup.parse(html);
//                    doc.text();
//                }
//                catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
//            @Override public void onFailure(int resCode, Header[] headers, byte[] bytes, Throwable throwable) {
//                Toast.makeText(UCampusActivity.this, "인터넷 상태 확인 필요.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}

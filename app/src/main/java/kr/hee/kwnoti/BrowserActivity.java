package kr.hee.kwnoti;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;

import kr.hee.kwnoti.u_campus_activity.Interface;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrowserActivity extends Activity {
    // 브라우저
    ProgressBar progressBar;
    Browser webView;
    Browser.ScrollChangedCallback fabVisibilityCallback;
    DownloadListener downloadListener;
    short scrollCount = 0;
    // FAB 버튼
    ImageButton fab;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        // 변수 및 뷰 초기화
        init();
        initView();

        // 넘어온 인텐트 데이터 분석. 유캠퍼스에서 넘어온 정적 HTML 파일이면 다르게 불러야함
        Bundle intentData = getIntent().getExtras();
        if (intentData == null) {
            UTILS.showToast(BrowserActivity.this, "에러 발생");
            return;
        }

        // 유캠퍼스에 의해 브라우저가 열렸는지 확인
        boolean isStartFromUcampus = intentData.getBoolean(KEY.BROWSER_FROM_UCAMPUS, false);

        // 제목 설정
        String title = intentData.getString(KEY.BROWSER_TITLE);
        setTitle(title);
//        getActionBar().setDisplayHomeAsUpEnabled(true);

        // 유캠퍼스를 통해 들어온 경우
        if (isStartFromUcampus)
            setForUCampus(intentData);
        // 일반적인 브라우징을 하는 경우
        else {
            // 액션바 제거
            getActionBar().hide();
            webView.loadUrl(intentData.getString(KEY.BROWSER_URL));
        }
    }

    /** 유캠퍼스로 브라우저를 켰을 때 세팅 */
    void setForUCampus(Bundle intentData) {
        // 유캠퍼스 접속 인터페이스 생성
        Interface request = UTILS.makeRequestClientForUCampus(this, Interface.LOGIN_URL);
        // 접속을 위한 데이터 추출
        String url = intentData.getString(KEY.BROWSER_URL);
        String bdSeq = intentData.getString(KEY.BROWSER_DATA);

        // 접속 시도
        Call<ResponseBody> call = request.getContent(url, "view", bdSeq);
        call.enqueue(new Callback<ResponseBody>() {
            @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> res) {
                try {
                    String body = res.body().string();
                    Document doc = Jsoup.parse(body);

                    // 본문
                    String html = doc.select("td.tl_l2").html();
                    html += "<br><font size=5em><b>※ 첨부파일</b></font><br>";

                    // 첨부파일
                    Elements files = doc.select("samp.link_b2 a");
                    for (Element file : files) {
                        String[] attr = file.toString().split("\'");
                        String serverFileName = attr[1];
                        String realFileName = attr[3];

                        html += "<a href=\"http://info2.kw.ac.kr/servlet/controller.library.DownloadServlet?p_savefile="
                                + serverFileName + "&p_realfile=" + realFileName + "\">"
                                + realFileName + "</a>";
                    }


                    webView.loadData(html, "text/html; charset=UTF-8", null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        UTILS.showToast(BrowserActivity.this, R.string.text_loadFailed);
                    }
                });
            }
        });
    }

    void init() {
        // 스크롤에 따른 FAB 버튼 보임/가림
        fabVisibilityCallback = new Browser.ScrollChangedCallback() {
            boolean fabVisible = true;
            Animation shrink = AnimationUtils.loadAnimation(BrowserActivity.this, R.anim.shrink);
            Animation expansion = AnimationUtils.loadAnimation(BrowserActivity.this, R.anim.expand);

            @Override public void onScroll(int cX, int cY, int oX, int oY) {
                final int delayAmount = 10;
                fabVisible = fab.getVisibility() == View.VISIBLE;

                // 스크롤 내릴 때
                if (cY > oY) {
                    scrollCount++;
                    if (scrollCount > delayAmount && fabVisible) {
                        fab.startAnimation(shrink);
                        fab.setVisibility(View.GONE);
                        scrollCount = 0;
                    }
                    else if (!fabVisible) scrollCount = 0;
                }
                else {
                    if (!fabVisible) {
                        fab.startAnimation(expansion);
                        fab.setVisibility(View.VISIBLE);
                        scrollCount = 0;
                    }
                }
            }
        };

        // 다운로드 관리자
        downloadListener = new DownloadListener() {
            @Override public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                                  String mimeType, long contentLength) {
                try {
                    // 헤더 설정
                    DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                    DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(url));
                    downloadRequest.setMimeType(mimeType);
                    downloadRequest.addRequestHeader("User-Agent", userAgent);
                    // 다운 받는 파일의 이름 설정
                    String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
                    // 파일 이름 디코드 및 오류 수정
                    if (fileName.endsWith(";")) fileName = fileName.replace(";", "");
                    fileName = URLDecoder.decode(fileName, "UTF-8");
                    // 다운로드 알림 설정 및 위치 지정
                    downloadRequest.setTitle(fileName);
                    downloadRequest.allowScanningByMediaScanner();
                    downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                    // 다운로드 시작
                    downloadManager.enqueue(downloadRequest);
                    UTILS.showToast(BrowserActivity.this, "다운로드 시작..");
                }
                catch (Exception e) {
                    // 파일 저장 권한
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                UTILS.showToast(BrowserActivity.this, "첨부파일을 받으려면 권한이 필요합니다.");
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 110);
                            } else {
                                UTILS.showToast(BrowserActivity.this, "첨부파일을 받으려면 권한이 필요합니다.");
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 110);
                            }
                        }
                    }
                }
            }
        };
    }

    void initView() {
        // 웹뷰 설정
        webView = (Browser)findViewById(R.id.browser_webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }
        });
        webView.setDownloadListener(downloadListener);
        webView.setOnScrollChangedCallback(fabVisibilityCallback);  // 스크롤에 따른 버튼 가시 설정
        WebSettings webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);        // 캐시 없음
        webSettings.setJavaScriptEnabled(true);                     // 자바스크립트 허용

        // FAB 버튼 설정
        fab = (ImageButton)findViewById(R.id.browser_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (webView.canGoBack()) webView.goBack();
                else finish();
            }
        });

        progressBar = (ProgressBar)findViewById(R.id.browser_progressBar);
    }

    // 뒤로가기 버튼을 누르면 뒤로 가든지 액티비티를 종료
    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        else return super.onKeyDown(keyCode, event);
    }
}

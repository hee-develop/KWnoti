package kr.hee.kwnoti;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
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
import org.jsoup.select.Elements;

import java.io.IOException;

import kr.hee.kwnoti.u_campus_activity.Interface;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class BrowserActivity extends Activity {
    ProgressBar progressBar;
    Browser webView;
    ImageButton fab;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        // 뷰 초기화
        initView();

        // 넘어온 인텐트 데이터 분석. 유캠퍼스에서 넘어온 정적 HTML 파일이면 다르게 불러야함
        Bundle intentData = getIntent().getExtras();
        boolean startFromUcampus = intentData.getBoolean(KEY.BROWSER_FROM_UCAMPUS, false);
        String title = intentData.getString(KEY.BROWSER_TITLE);
        setTitle(title);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        if (startFromUcampus) {
            Interface request = UTILS.makeRequestClientForUCampus(this, Interface.LOGIN_URL);

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

    // 뒤로가기 버튼을 누르면 뒤로 가든지 액티비티를 종료함
    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack())
            webView.goBack();
        return super.onKeyDown(keyCode, event);
    }

    //

    // 스크롤에 따른 FAB 버튼 보임/가림
    Browser.ScrollChangedCallback fabVisibilityCallback = new Browser.ScrollChangedCallback() {
        boolean fabVisible = true;
        short scrollCount = 0;
        Animation shrink = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shrink);
        Animation expansion = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.expand);

        @Override public void onScroll(int cX, int cY, int oX, int oY) {
            final int delayAmount = 2;
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
                scrollCount--;
                if (scrollCount < -delayAmount && !fabVisible) {
                    fab.startAnimation(expansion);
                    fab.setVisibility(View.VISIBLE);
                    scrollCount = 0;
                }
            }
        }
    };

    // 첨부파일 다운로드 리스너
    DownloadListener downloadListener = new DownloadListener() {
        @Override public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                              String mimeType, long contentLength) {
            try {
                DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(url));
                downloadRequest.setMimeType(mimeType);
                downloadRequest.addRequestHeader("User-Agent", userAgent);
                String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType); // 파일 이름 추출
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
                            == PERMISSION_DENIED) {
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

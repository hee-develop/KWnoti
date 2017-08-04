package kr.hee.kwnoti;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Intent;
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

import kr.hee.kwnoti.u_campus_activity.UCamConnection;
import kr.hee.kwnoti.u_campus_activity.UCamConnectionInterface;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** 웹 브라우저 액티비티 */
public class BrowserActivity extends Activity {
    // 브라우저
    ProgressBar progressBar; // 브라우저 로딩 바
    Browser webView; // 브라우저(웹뷰)
    Browser.ScrollChangedCallback fabVisibilityCallback; // 브라우저 스크롤 리스너
    DownloadListener downloadListener; // 다운로드 관리자
    short scrollCount = 0;
    // FAB 버튼
    ImageButton fab;

    /** 새로운 인텐트가 들어왔을 때 불리는 메소드 */
    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // null 예외처리
        if (intent == null) {
            UTILS.showToast(this, "URL을 찾을 수 없습니다.");
            return;
        }

        // 인텐트 내 번들 추출
        Bundle intentData = intent.getExtras();

        // 제목 설정
        setTitle(intentData.getString(KEY.BROWSER_TITLE));

        // 유캠퍼스에 의해 불렸는지 확인
        boolean callFromUcampus = intentData.getBoolean(KEY.BROWSER_FROM_UCAMPUS, false);
        boolean callFromLectureNote = intentData.getBoolean(KEY.BROWSER_FROM_LECTURE_NOTE, false);

        // 유캠퍼스 혹은 일반 웹페이지에 따라 다르게 로드
        if (callFromUcampus)            loadUcampus(intentData);
        else if (callFromLectureNote)   loadLectureNote(intentData);
        else                            loadWebPage(intentData);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        // 변수 및 뷰 초기화
        init();
        initView();

        // 인텐트 값을 통한 페이지 로드
        onNewIntent(getIntent());
    }

    /** 유캠퍼스의 내용을 표시하는 메소드 */
    void loadUcampus(Bundle uCampusData) {
        // 액션바 생성
        ActionBar actionBar = getActionBar();
        if (actionBar != null) actionBar.show();

        // 유캠퍼스 접속 인터페이스 생성
        UCamConnectionInterface request = UCamConnection.getInstance().getUCamInterface();
        // 접속을 위한 데이터 추출
        String url = uCampusData.getString(KEY.BROWSER_URL);
        String bdSeq = uCampusData.getString(KEY.BROWSER_DATA);

        // 접속 시도
        Call<ResponseBody> call = request.getContent(url, "view", bdSeq);
        call.enqueue(new Callback<ResponseBody>() {
            @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> res) {
                try {
                    String body = res.body().string();
                    Document doc = Jsoup.parse(body);

                    // 본문 내용 추출
                    StringBuilder contents = new StringBuilder(doc.select("td.tl_l2").html());
                    contents.append("<br><hr noshade size=1px><p><font size=4.2em><b>※ 첨부파일</b></font></p>");

                    // 첨부파일 추출 및 본문 내용에 추가
                    Elements files = doc.select("samp.link_b2 a");
                    for (Element file : files) {
                        String[] attr = file.toString().split("\'");
                        String serverFileName = attr[1];
                        String realFileName = attr[3];

                        // 본문 내용에 추가
                        contents.append("<a href=\"http://info2.kw.ac.kr/servlet/controller.library.DownloadServlet?p_savefile=")
                                .append(serverFileName)
                                .append("&p_realfile=")
                                .append(realFileName)
                                .append("\">")
                                .append(realFileName)
                                .append("</a><br>");
                    }
                    // 페이지 로드
                    webView.loadData(contents.toString(), "text/html; charset=UTF-8", null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        UTILS.showToast(BrowserActivity.this, R.string.toast_loadFailed);
                    }
                });
            }
        });
    }

    /** 강의자료를 표시하는 메소드 */
    void loadLectureNote(Bundle lectureNoteData) {
        final StringBuilder sbURL = new StringBuilder("http://info.kw.ac.kr/webnote/lecture/");
        if (!lectureNoteData.getBoolean(KEY.BROWSER_INCLUDE_URL)) {
            // 강의 ID 추출 및 예외처리
            char[] subjectId = lectureNoteData.getString(KEY.BROWSER_DATA, "").toCharArray();
            if (subjectId.length == 0) {
                UTILS.showToast(this, "잘못된 접근입니다.");
                return;
            }

            // 강의 ID를 분리해 URL 인자로 사용
            final char[] year   = { subjectId[1], subjectId[2], subjectId[3], subjectId[4] };
            final char   semester= subjectId[5];
            final char[] subjId  = { subjectId[6], subjectId[7], subjectId[8], subjectId[9] },
                    major   = { subjectId[10], subjectId[11], subjectId[12], subjectId[13] },
                    subClass= { subjectId[14], subjectId[15] };
            final char   grade   = subjectId[16];

            // URL 쿼리 삽입
            sbURL.append("h_lecture01_2.php?layout_opt=N&engineer_code=&skin_opt=&")
                    .append("fsel1_code=&fsel1_str=&fsel2_code=&fsel2_str=&fsel2=00_00&")
                    .append("fsel3=&fsel4=00_00&hh=&sugang_opt=all&tmp_key=tmp__stu&")
                    .append("bunban_no=").append(subClass)
                    .append("&hakgi=").append(semester)
                    .append("&open_grade=").append(grade)
                    .append("&open_gwamok_no=").append(subjId)
                    .append("&open_major_code=").append(major)
                    .append("&this_year=").append(year);
        }
        else {
            sbURL.append(lectureNoteData.getString(KEY.BROWSER_URL));
        }
        


        // 유캠퍼스 로그인
        final UCamConnectionInterface request = UCamConnection.getInstance().getUCamInterface();

        // 스레드를 통해 강의계획서를 불러옴
        new Thread(new Runnable() {
            @Override public void run() {
                // 강의계획서 접속
                Call<ResponseBody> call = request.getLectureNote(sbURL.toString());
                try {
                    Response<ResponseBody> response = call.execute();
                    String resBody = new String(response.body().bytes(), "EUC-KR");
                    resBody = resBody.replaceAll("width=750", "width=100%");
                    resBody = resBody.replace("<script type=\"text/javascript\" src=\"/style/common.js\"></script>",
                            "<style>body, table {" +
                                    "font-size: 1.2em;"
                                    + "}</style>");

                    final String finalResBody = resBody;
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            webView.loadData(finalResBody, "text/html; charset=UTF-8", null);
                            WebSettings settings = webView.getSettings();
                            settings.setLoadWithOverviewMode(true);
                            settings.setUseWideViewPort(true);
                        }
                    });
                }
                catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            UTILS.showToast(BrowserActivity.this, R.string.toast_loadFailed);
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /** 일반 웹페이지의 내용을 표시하는 메소드 */
    void loadWebPage(Bundle webPageData) {
        // 액션바 제거
        ActionBar actionBar = getActionBar();
        if (actionBar != null) actionBar.hide();

        // 웹사이트 로드
        webView.loadUrl(webPageData.getString(KEY.BROWSER_URL));
    }

    /** 변수 및 리스너 초기화 */
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
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BrowserActivity.this);
                                dialogBuilder.setMessage("첨부파일을 받으려면 권한이 필요합니다.").create();
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 110);
                            } else {
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BrowserActivity.this);
                                dialogBuilder.setMessage("첨부파일을 받으려면 권한이 필요합니다.").create();
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 110);
                            }
                        }
                    }
                }
            }
        };
    }

    /** 뷰 초기화 및 리스너 연결 */
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

    /** 뒤로가기 버튼을 누르면 뒤로 가든지 액티비티를 종료 */
    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        else return super.onKeyDown(keyCode, event);
    }
}

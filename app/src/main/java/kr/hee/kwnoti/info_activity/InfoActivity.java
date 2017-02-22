package kr.hee.kwnoti.info_activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import kr.hee.kwnoti.R;
import kr.hee.kwnoti.UTILS;

public class InfoActivity extends Activity {
    // 리사이클러 뷰 친구들
    RecyclerView    recyclerView;
    InfoAdapter     adapter;
    LinearLayoutManager layoutManager;
    // 로딩 다이얼로그
    ProgressDialog  progressDialog;
    // 검색 뷰 친구들
    LinearLayout    search_layout;
    EditText        search_editText;
    Spinner         search_spinner;
    // 뷰 옵션
    boolean showTopNotify = true;   // 상단 공지 표시 여부
    int     currentPage = 0;        // 현재 페이지
    // 크롤러 옵션
    static final String[] options = { "전체", "일반", "학사", "학생", "봉사", "등록/장학",
            "입학", "시설", "병무", "외부" };
    String groupType = options[0];       // 기본값 : 전체
    static final String[] parserOptions = { "전체", "제목", "내용", "제목 + 내용", "작성자" };
    String parserType= parserOptions[0]; // 기본값 : 전체

    // 다이얼로그 생성(출력이 아님!)
    @Override protected void onStart() {
        super.onStart();
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.dialog_loading));
    }

    // 메뉴 버튼 인플레이트
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    // 메뉴 버튼 클릭 이벤트 리스너
    @Override public boolean onMenuItemSelected(int clickId, final MenuItem item) {
        switch (item.getItemId())
        {
            // 검색 버튼 선택
            case R.id.info_toolbar_search :
                if (search_layout.getVisibility() == View.VISIBLE) {
                    search_layout.setVisibility(View.GONE);
                    UTILS.disappearKeyboard(InfoActivity.this, search_editText);
                }
                else {
                    search_layout.setVisibility(View.VISIBLE);
                    UTILS.appearKeyboard(InfoActivity.this, search_editText);
                }
                break;
            // 글 그룹 선택
            case R.id.info_toolbar_group :
                UTILS.showAlertDialog(InfoActivity.this, "분류를 선택하세요", options,
                        new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dInterface, int i) {
                                // 클릭한 값에 맞게 메뉴 글씨 변경 및 실제 값 변경
                                item.setTitle(groupType = options[i]);
                                // 옵션에 맞는 데이터 로드
                                currentPage = 0;
                                adapter.cleanInfo();
                                showTopNotify = true;
                                new ParserThread().start();
                            }
                        }
                );
                break;
        }
        return true;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setTitle(R.string.info_title);
        initView();
        // 파싱 시작 TODO join() 메소드 불가...
        new ParserThread().start();
    }

    void initView() {
        recyclerView    = (RecyclerView)findViewById(R.id.info_recyclerView);
        search_layout   = (LinearLayout)findViewById(R.id.info_search_layout);
        search_editText = (EditText)findViewById(R.id.info_search_editText);
        search_spinner  = (Spinner)findViewById(R.id.info_search_spinner);

        // 리사이클러 뷰 스크롤 리스너 설정 및 구성
        RecyclerView.OnScrollListener scrollListener = endlessScrollListener;
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter = new InfoAdapter(InfoActivity.this));
        recyclerView.addOnScrollListener(scrollListener);

        search_editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override public boolean onEditorAction(TextView textView, int action, KeyEvent event) {
                if (action == EditorInfo.IME_ACTION_SEARCH) {
                    currentPage = 0;
                    adapter.cleanInfo();
                    showTopNotify = true;
                    parserType = search_spinner.getSelectedItem().toString();
                    new ParserThread().start();
                    UTILS.disappearKeyboard(InfoActivity.this, search_editText);
                }
                return false;
            }
        });
    }

    // 무한 스크롤링을 위한 리스너
    RecyclerView.OnScrollListener endlessScrollListener = new RecyclerView.OnScrollListener() {
        int previousTotal = 0;      // 전체 데이터의 개수
        boolean loading = false;    // 일종의 semaphore. 로딩이 될 때까지 카운트하지 않음
        int visibleThreshold = 2;   // 남은 뷰의 개수. 해당 개수만큼 남으면 불러오기를 시작
        int firstVisibleItem, visibleItemCount, totalItemCount;

        @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            visibleItemCount    = recyclerView.getChildCount();
            totalItemCount      = layoutManager.getItemCount();
            firstVisibleItem    = layoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            // 데이터가 7개(화면에 들어가는 대략적인 개수)보다 적을 때,
            // 같은 데이터를 불러 오는 낭비를 하지 않게 하기 위해 설정
            if (totalItemCount > 7) {
                if (!loading && (totalItemCount - visibleItemCount)
                        <= firstVisibleItem + visibleThreshold) {
                    // 다음 쪽 로딩
                    currentPage += 10;
                    showTopNotify = false;
                    new ParserThread().start();
                    loading = true;
                }
            }
        }
    };

    @Override protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    class ParserThread extends Thread {
        static final int TIMEOUT = 10000;
        boolean noWaitForResponse = false;

        @Override public void run() {
            super.run();

            // 로딩 중 다이얼로그 표시
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    progressDialog.show();
                }
            });

            // 검색 기능 활성화 여부 확인 및 URL 생성
            String url = "http://www.kw.ac.kr/ko/life/notice.do?mode=list&&articleLimit=10";

            // 그룹의 종류에 따라 URL 결정
            switch (groupType)
            {
                default : // 에러 시 전체로 검색
                case "전체" : url += "&srCategoryId1="; break;
                case "일반" : url += "&srCategoryId1=5"; break;
                case "학사" : url += "&srCategoryId1=6"; break;
                case "학생" : url += "&srCategoryId1=16"; break;
                case "봉사" : url += "&srCategoryId1=17"; break;
                case "등록/장학" : url += "&srCategoryId1=18"; break;
                case "입학" : url += "&srCategoryId1=19"; break;
                case "시설" : url += "&srCategoryId1=20"; break;
                case "병무" : url += "&srCategoryId1=21"; break;
                case "외부" : url += "&srCategoryId1=22"; break;
            }

            // 파서의 종류에 따라 URL 결정
            switch (parserType)
            {
                case "제목" :
                    url += "&srSearchKey=article_title"; break;
                case "내용" :
                    url += "&srSearchKey=article_text"; noWaitForResponse = true; break;
                case "제목 + 내용" :
                    noWaitForResponse = true; break;
                case "작성자" :
                    url += "&srSearchKey=writer_nm"; break;
                default : break;
            }

            // 현재 페이지 설정
            url += "&article.offset=" + currentPage;

            // 검색 기능이 활성화된 경우 검색 문구 추가. URLEncoder 클래스 사용 불필요.
            if (!parserType.equals("전체"))
                url += "&srSearchVal=" + search_editText.getText().toString();

            // 파싱 시작 =================================================
            try {
                Document doc;
                if (noWaitForResponse)  doc = Jsoup.connect(url).get();
                else                    doc = Jsoup.connect(url).timeout(TIMEOUT).get();

                Elements elements = doc.select("li");
                for (Element element : elements) {
                    // 상단 공지 출력 여부 결정(다음 쪽으로 넘어갈 땐 상단 공지 제거)
                    boolean isTopNotice = element.hasClass("top-notice");
                    if (isTopNotice && !showTopNotify) continue;

                    // 공지사항 선택
                    Element notify = element.select("div.board-text").first();
                    if (notify == null) continue; // 선택 오류가 났을 때 넘김

                    String  title       = notify.select("a").text(),
                            content[]   = notify.select("p").text().split(" \\| ");
                    boolean isNewInfo   = title.contains("신규게시글"),
                            hasAttachment = title.contains("Attachment");
                    // 제목 외 필요 없는 데이터 분리
                    title = title.replace("신규게시글", "").replace("Attachment", "");

                    // 분리한 데이터로 객체 생성 및 어댑터에 삽입
                    final InfoData infoData = new InfoData(title, content[2], content[1], content[0],
                            "http://www.kw.ac.kr/ko/life/notice.do" + notify.select("a").attr("href"),
                            isNewInfo, hasAttachment);
                    adapter.addInfo(infoData);
                }

                // 파싱이 끝나면 데이터가 변경됐음을 알림
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
            catch (IOException e) {
                // 대기시간이 초과됐거나 인터넷 연결이 안되있는 경우
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        Toast.makeText(InfoActivity.this, getString(R.string.toast_failed),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            finally {
                // 에러가 났든 제대로 반영됐든 다이얼로그는 없애줌
                progressDialog.dismiss();
            }
        }
    }
}
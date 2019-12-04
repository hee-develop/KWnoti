package kr.hee.kwnoti.info_activity;

import android.app.Activity;
import android.os.Bundle;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.hee.kwnoti.R;


public class InfoActivity extends Activity implements InfoDataReceived {
    // recycler view
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    InfoAdapter adapter;

    // url and query
    UrlWithQuery url;
    int currNoticeNumber = 0;

    ArrayList<InfoData> infoDataArr = new ArrayList<>();

    @Override
    protected void onStart() {
        super.onStart();

        url = new UrlWithQuery("https://www.kw.ac.kr/ko/life/notice.do?mode=list&&articleLimit=10");

        new RequestInfoThread(url.getUrl()).start();
    }

    public class RequestInfoThread extends RequestThread {
        public RequestInfoThread(String baseUrl) {
            super(baseUrl);
        }

        @Override
        public void afterReceived(Document doc) {
            ArrayList<InfoData> infoArray = new ArrayList<>();

            // if failed
            if (doc == null) {
                return;
            }

            // get information list
            Elements elements = doc.select("div.board-list-box>ul>li");
            for (Element e : elements) {
                Elements title = e.select("a");
                String[] desc = e.select("p").text().split(" \\| ");
                if (desc.length != 3) desc = new String[3];

                InfoData eInfo = new InfoData(
                        title.text().replaceAll("신규게시글|Attachment", ""),
                        title.attr("href"),
                        desc[1]);

                if (e.hasClass("top-notice")) {
                    eInfo.isTopTitle = true;
                }
                infoArray.add(eInfo);
            }

            // send arrayList
            makeView(infoArray);
        }
    }

    @Override
    public void makeView(ArrayList infoArr) {
        // add data to data array
        for (Object i : infoArr) {
            infoDataArr.add((InfoData)i);
        }

        runOnUiThread(() -> adapter.notifyDataSetChanged());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_info);
        recyclerView    = findViewById(R.id.info_recyclerView);
        recyclerView.setLayoutManager(layoutManager = new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter = new InfoAdapter(InfoActivity.this, infoDataArr));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(1)) {
                    url.setUrlQuery("article.offset", currNoticeNumber += 10);
                    new RequestInfoThread(url.getUrl()).start();
                }
                else super.onScrollStateChanged(recyclerView, newState);
            }
        });

//        RecyclerView.OnScrollListener scrollListener = endlessScrollListener;
//        recyclerView.addOnScrollListener(scrollListener);


        // 무한 스크롤링을 위한 리스너
//    RecyclerView.OnScrollListener endlessScrollListener = new RecyclerView.OnScrollListener() {
//        int previousTotal = 0;      // 전체 데이터의 개수
//        boolean loading = false;    // 일종의 semaphore. 로딩이 될 때까지 카운트하지 않음
//        int visibleThreshold = 2;   // 남은 뷰의 개수. 해당 개수만큼 남으면 불러오기를 시작
//        int firstVisibleItem, visibleItemCount, totalItemCount;
//
//        @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            super.onScrolled(recyclerView, dx, dy);
//            visibleItemCount    = recyclerView.getChildCount();
//            totalItemCount      = layoutManager.getItemCount();
//            firstVisibleItem    = layoutManager.findFirstVisibleItemPosition();
//
//            if (loading) {
//                if (totalItemCount > previousTotal) {
//                    loading = false;
//                    previousTotal = totalItemCount;
//                }
//            }
//            // 데이터가 7개(화면에 들어가는 대략적인 개수)보다 적을 때,
//            // 같은 데이터를 불러 오는 낭비를 하지 않게 하기 위해 설정
//            if (totalItemCount > 7) {
//                if (!loading && (totalItemCount - visibleItemCount)
//                        <= firstVisibleItem + visibleThreshold) {
//                    // 다음 쪽 로딩
//                    currentPage += 10;
//                    showTopNotify = false;
//                    new ParserThread().start();
//                    loading = true;
//                }
//            }
//        }
//    };

//        search_layout   = (LinearLayout)findViewById(R.id.info_search_layout);
//        search_editText = (EditText)findViewById(R.id.info_search_editText);
//        search_spinner  = (Spinner)findViewById(R.id.info_search_spinner);
//
//        // 리사이클러 뷰 스크롤 리스너 설정 및 구성
//        RecyclerView.OnScrollListener scrollListener = endlessScrollListener;
        recyclerView.setLayoutManager(layoutManager = new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter = new InfoAdapter(InfoActivity.this, infoDataArr));
//        recyclerView.addOnScrollListener(scrollListener);
//
//        search_editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override public boolean onEditorAction(TextView textView, int action, KeyEvent event) {
//                if (action == EditorInfo.IME_ACTION_SEARCH) {
//                    currentPage = 0;
//                    adapter.cleanInfo();
//                    showTopNotify = true;
//                    parserType = search_spinner.getSelectedItem().toString();
//                    new ParserThread().start();
//                    UTILS.disappearKeyboard(InfoActivity.this, search_editText);
//                }
//                return false;
//            }
//        });
//
//        toolbar = (Toolbar)findViewById(R.id.toolbar_view);
//        toolbar_title = (TextView)findViewById(R.id.kwtoolbar_title);
////        setSupportActionBar(toolbar);
////        getActionBar().setDisplayShowTitleEnabled(false);
////        getActionBar().setDisplayShowTitleEnabled(false);
////        getActionBar().setDisplayShowCustomEnabled(true);
////        getActionBar().setDisplayUseLogoEnabled(false);
////        getActionBar().setDisplayShowHomeEnabled(false);
//
//        setTitle(R.string.info_title);
//        initView();
//
//        // 파싱 시작
//        new ParserThread().start();
    }
}



//public class InfoActivity extends ActivityLoadingBase {
//    Toolbar toolbar;
//    TextView toolbar_title;
//
//    // 리사이클러 뷰 친구들
//    RecyclerView    recyclerView;
//    InfoAdapter     adapter;
//    LinearLayoutManager layoutManager;
//    // 검색 뷰 친구들
//    LinearLayout    search_layout;
//    EditText        search_editText;
//    Spinner         search_spinner;
//    // 뷰 옵션
//    boolean showTopNotify = true;   // 상단 공지 표시 여부
//    int     currentPage = 0;        // 현재 페이지
//    // 크롤러 옵션
//    static final String[] options = { "전체", "일반", "학사", "학생", "봉사", "등록/장학",
//            "입학", "시설", "병무", "외부" };
//    String groupType = options[0];       // 기본값 : 전체
//    static final String[] parserOptions = { "전체", "제목", "내용", "제목 + 내용", "작성자" };
//    String parserType= parserOptions[0]; // 기본값 : 전체
//
//    // 메뉴 버튼 인플레이트
//    @Override public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_info, menu);
//
//        RelativeLayout toolbarLayout = (RelativeLayout)toolbar.getChildAt(0);
//        ActionMenuView menuView = (ActionMenuView)toolbar.getChildAt(2);
////        menuView.setOrientation(LinearLayout.HORIZONTAL);
////        menuView.getLayoutParams()
//
//        toolbar.removeView(menuView);
//        toolbarLayout.addView(menuView);
//
//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)menuView.getLayoutParams();
//        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
////        layoutParams.width = 1500;
//        menuView.setLayoutParams(layoutParams);
//
//        return true;
//    }
//
//    // 메뉴 버튼 클릭 이벤트 리스너
//    @Override public boolean onMenuItemSelected(int clickId, final MenuItem item) {
//        switch (item.getItemId())
//        {
//            // 검색 버튼 선택
//            case R.id.info_toolbar_search :
//                if (search_layout.getVisibility() == View.VISIBLE) {
//                    search_layout.setVisibility(View.GONE);
//                    UTILS.disappearKeyboard(InfoActivity.this, search_editText);
//                }
//                else {
//                    search_layout.setVisibility(View.VISIBLE);
//                    UTILS.appearKeyboard(InfoActivity.this, search_editText);
//                }
//                break;
//            // 글 그룹 선택
//            case R.id.info_toolbar_group :
//                UTILS.showAlertDialog(InfoActivity.this, "분류를 선택하세요", options, // TODO 다국어지원
//                        new DialogInterface.OnClickListener() {
//                            @Override public void onClick(DialogInterface dInterface, int i) {
//                                // 클릭한 값에 맞게 메뉴 글씨 변경 및 실제 값 변경
//                                item.setTitle(groupType = options[i]);
//                                // 옵션에 맞는 데이터 로드
//                                currentPage = 0;
//                                adapter.cleanInfo();
//                                showTopNotify = true;
//                                new ParserThread().start();
//                            }
//                        }
//                );
//                break;
//        }
//        return true;
//    }
//
//    @Override protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_info);
//
//        toolbar = (Toolbar)findViewById(R.id.toolbar_view);
//        toolbar_title = (TextView)findViewById(R.id.kwtoolbar_title);
////        setSupportActionBar(toolbar);
////        getActionBar().setDisplayShowTitleEnabled(false);
////        getActionBar().setDisplayShowTitleEnabled(false);
////        getActionBar().setDisplayShowCustomEnabled(true);
////        getActionBar().setDisplayUseLogoEnabled(false);
////        getActionBar().setDisplayShowHomeEnabled(false);
//
//        setTitle(R.string.info_title);
//        initView();
//
//        // 파싱 시작
//        new ParserThread().start();
//    }
//
//    void initView() {
//        recyclerView    = (RecyclerView)findViewById(R.id.info_recyclerView);
//        search_layout   = (LinearLayout)findViewById(R.id.info_search_layout);
//        search_editText = (EditText)findViewById(R.id.info_search_editText);
//        search_spinner  = (Spinner)findViewById(R.id.info_search_spinner);
//
//        // 리사이클러 뷰 스크롤 리스너 설정 및 구성
//        RecyclerView.OnScrollListener scrollListener = endlessScrollListener;
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(adapter = new InfoAdapter(InfoActivity.this));
//        recyclerView.addOnScrollListener(scrollListener);
//
//        search_editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override public boolean onEditorAction(TextView textView, int action, KeyEvent event) {
//                if (action == EditorInfo.IME_ACTION_SEARCH) {
//                    currentPage = 0;
//                    adapter.cleanInfo();
//                    showTopNotify = true;
//                    parserType = search_spinner.getSelectedItem().toString();
//                    new ParserThread().start();
//                    UTILS.disappearKeyboard(InfoActivity.this, search_editText);
//                }
//                return false;
//            }
//        });
//    }
//
//    // 무한 스크롤링을 위한 리스너
//    RecyclerView.OnScrollListener endlessScrollListener = new RecyclerView.OnScrollListener() {
//        int previousTotal = 0;      // 전체 데이터의 개수
//        boolean loading = false;    // 일종의 semaphore. 로딩이 될 때까지 카운트하지 않음
//        int visibleThreshold = 2;   // 남은 뷰의 개수. 해당 개수만큼 남으면 불러오기를 시작
//        int firstVisibleItem, visibleItemCount, totalItemCount;
//
//        @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            super.onScrolled(recyclerView, dx, dy);
//            visibleItemCount    = recyclerView.getChildCount();
//            totalItemCount      = layoutManager.getItemCount();
//            firstVisibleItem    = layoutManager.findFirstVisibleItemPosition();
//
//            if (loading) {
//                if (totalItemCount > previousTotal) {
//                    loading = false;
//                    previousTotal = totalItemCount;
//                }
//            }
//            // 데이터가 7개(화면에 들어가는 대략적인 개수)보다 적을 때,
//            // 같은 데이터를 불러 오는 낭비를 하지 않게 하기 위해 설정
//            if (totalItemCount > 7) {
//                if (!loading && (totalItemCount - visibleItemCount)
//                        <= firstVisibleItem + visibleThreshold) {
//                    // 다음 쪽 로딩
//                    currentPage += 10;
//                    showTopNotify = false;
//                    new ParserThread().start();
//                    loading = true;
//                }
//            }
//        }
//    };
//
//    /** 로딩화면을 중간에 멈추면 스레드 제거 */
//    @Override public void loadCanceled() {
//        // 현재 동작중인 ParserThread 검색
//        ThreadGroup tg = Thread.currentThread().getThreadGroup();
//        while (true) {
//            ThreadGroup tg2 = tg.getParent();
//            if (tg2 != null) tg = tg2;
//            else break;
//        }
//        Thread[] threads = new Thread[64];
//        tg.enumerate(threads); // 현재 스레드를 배열에 삽입
//
//        // 스레드 동작 중지 요청. interrupt()를 한다고 해도 바로 종료되지는 않기 때문에 flag 추가 설정
//        for (Thread thread : threads) {
//            if (thread != null && thread.getName().equals(THREAD_NAME)) {
//                // 스레드 중지 요청
//                thread.interrupt();
//                // 스레드 내 반복문 강제 중지
//                ((ParserThread)thread).stopLoop = true;
//
//                break;
//            }
//        }
//    }
//
//    private final static String THREAD_NAME = "InfoThread";
//
//    private class ParserThread extends Thread {
//        static final int TIMEOUT = 10000;
//        boolean noWaitForResponse = false;
//
//        // 중단에 멈추도록 해주는 플래그
//        boolean stopLoop;
//
//        @Override public void run() {
//            super.run();
//
//            // 스레드 이름과 플래그 초기화
//            setName(THREAD_NAME);
//            stopLoop = false;
//
//            // 로딩 중 다이얼로그 표시
//            loadStart();
//
//            // 검색 기능 활성화 여부 확인 및 URL 생성
//            String url = "http://www.kw.ac.kr/ko/life/notice.do?mode=list&&articleLimit=10";
//
//            // 그룹의 종류에 따라 URL 결정
//            switch (groupType)
//            {
//                default : // 에러 시 전체로 검색
//                case "전체" : url += "&srCategoryId1="; break;
//                case "일반" : url += "&srCategoryId1=5"; break;
//                case "학사" : url += "&srCategoryId1=6"; break;
//                case "학생" : url += "&srCategoryId1=16"; break;
//                case "봉사" : url += "&srCategoryId1=17"; break;
//                case "등록/장학" : url += "&srCategoryId1=18"; break;
//                case "입학" : url += "&srCategoryId1=19"; break;
//                case "시설" : url += "&srCategoryId1=20"; break;
//                case "병무" : url += "&srCategoryId1=21"; break;
//                case "외부" : url += "&srCategoryId1=22"; break;
//            }
//
//            // 파서의 종류에 따라 URL 결정
//            switch (parserType)
//            {
//                case "제목" :
//                    url += "&srSearchKey=article_title"; break;
//                case "내용" :
//                    url += "&srSearchKey=article_text"; noWaitForResponse = true; break;
//                case "제목 + 내용" :
//                    noWaitForResponse = true; break;
//                case "작성자" :
//                    url += "&srSearchKey=writer_nm"; break;
//                default : break;
//            }
//
//            // 현재 페이지 설정
//            url += "&article.offset=" + currentPage;
//
//            // 검색 기능이 활성화된 경우 검색 문구 추가. URLEncoder 클래스 사용 불필요.
//            if (!parserType.equals("전체"))
//                url += "&srSearchVal=" + search_editText.getText().toString();
//
//            // 파싱 시작 =================================================
//            try {
//                Document doc;
//                if (noWaitForResponse)  doc = Jsoup.connect(url).get();
//                else                    doc = Jsoup.connect(url).timeout(TIMEOUT).get();
//
//                Elements elements = doc.select("li");
//                for (Element element : elements) {
//                    // 멈추라는 신호가 있는 경우 중단
//                    if (stopLoop) return;
//
//                    // 상단 공지 출력 여부 결정(다음 쪽으로 넘어갈 땐 상단 공지 제거)
//                    boolean isTopNotice = element.hasClass("top-notice");
//                    if (isTopNotice && !showTopNotify) continue;
//
//                    // 공지사항 선택
//                    Element notify = element.select("div.board-text").first();
//                    if (notify == null) continue; // 선택 오류가 났을 때 넘김
//
//                    String  title       = notify.select("a").text(),
//                            content[]   = notify.select("p").text().split(" \\| ");
//                    boolean isNewInfo   = title.contains("신규게시글"),
//                            hasAttachment = title.contains("Attachment");
//                    // 제목 외 필요 없는 데이터 분리
//                    title = title.replace("신규게시글", "").replace("Attachment", "");
//
//                    // 분리한 데이터로 객체 생성 및 어댑터에 삽입
//                    final InfoData infoData = new InfoData(title, content[2], content[1], content[0],
//                            "http://www.kw.ac.kr/ko/life/notice.do" + notify.select("a").attr("href"),
//                            isNewInfo, hasAttachment);
//                    adapter.addInfo(infoData);
//                }
//
//                // 멈추라는 신호가 있는 경우 중단
//                if (stopLoop) return;
//
//                // 파싱이 끝나면 데이터가 변경됐음을 알림
//                runOnUiThread(new Runnable() {
//                    @Override public void run() {
//                        adapter.notifyDataSetChanged();
//                    }
//                });
//            }
//            catch (IOException e) {
//                // 대기시간이 초과됐거나 인터넷 연결이 안되있는 경우
//                e.printStackTrace();
//                runOnUiThread(new Runnable() {
//                    @Override public void run() {
//                        Toast.makeText(InfoActivity.this, getString(R.string.toast_failed),
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//            finally {
//                loadFinish();
//            }
//        }
//    }
//}
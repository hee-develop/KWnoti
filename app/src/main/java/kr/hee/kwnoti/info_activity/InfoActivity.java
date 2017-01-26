package kr.hee.kwnoti.info_activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import kr.hee.kwnoti.R;
import kr.hee.kwnoti.info_activity.InfoAdapter;
import kr.hee.kwnoti.info_activity.InfoData;

/** 공지사항 액티비티 */
public class InfoActivity extends Activity {
    RecyclerView recyclerView;
    InfoAdapter adapter;
    ProgressDialog progressDialog;
    LinearLayoutManager layoutManager;
    ImageView btn_refresh;

    private int currentPage = 0;
    private boolean showTopNotify = true;
    ParserThread parserThread = new ParserThread();

    @Override protected void onStart() {
        super.onStart();
        // 로딩 다이얼로그 생성 (출력은 Thread 에서 관리)
        progressDialog = new ProgressDialog(this, android.R.style.Theme_Material_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.dialog_loading));
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setTitle(getString(R.string.info_title));

        btn_refresh = (ImageView)findViewById(R.id.info_toolbar_find);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

            }
        });

        // 리사이클러 뷰 스크롤 리스너 (자동 더보기) 설정 및 레이아웃 설정
        layoutManager = new LinearLayoutManager(this);
        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
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
                if (!loading && (totalItemCount - visibleItemCount)
                        <= firstVisibleItem + visibleThreshold) {
                    currentPage += 10;
                    showTopNotify = false;
                    new ParserThread().start();
                    loading = true;
                }
            }
        };

        // 리사이클러 뷰 연결(어댑터는 스레드에서 추가됨)
        recyclerView = (RecyclerView)findViewById(R.id.info_recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter = new InfoAdapter(getApplicationContext()));
        recyclerView.addOnScrollListener(scrollListener);

        // 홈페이지 파싱
        parserThread.start();
        // 왠지는 모르겠으나 'join'을 통한 dialog dismiss 불가능
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    /** Jsoup을 이용한 홈페이지 파서 스레드 */
    class ParserThread extends Thread {
        @Override public void run() {
            super.run();
            // 로딩 중 다이얼로그 표시
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    progressDialog.show();
                }
            });

            // 한 번에 10개의 게시글을 불러 옴(기본값임)
            String url = "http://www.kw.ac.kr/ko/life/notice.do?mode=list&&articleLimit=10&" +
                    "article.offset=" + currentPage;
            try {
                Document doc = Jsoup.connect(url).timeout(5000).get();
                Elements elements = doc.select("li");

                for (Element element : elements) {

                    // 상단 공지를 보이지 않게 설정하면 출력에서 제외
                    boolean elementIsTopNotice = element.hasClass("top-notice");
                    if (elementIsTopNotice && !showTopNotify) continue;

                    Element notify = element.select("div.board-text").first();
                    if (notify != null) {
                        String title = notify.select("a").text(),                       // 제목
                                content[] = notify.select("p").text().split(" \\| ");   // 조회수
                        boolean newInfo = title.contains("신규게시글"),                   // 새 글
                                attachment = title.contains("Attachment");              // 첨부 파일
                        // 타이틀에서 제목 외 데이터 분리
                        title = title.replace("신규게시글", "");
                        title = title.replace("Attachment", "");
                        // 객체 생성 및 어댑터에 삽입
                        final InfoData infoData = new InfoData(title, content[2], content[1], content[0],
                                "http://www.kw.ac.kr/ko/life/notice.do" + notify.select("a").attr("href"),
                                newInfo, attachment);
                        adapter.addInfo(infoData);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
            catch (IOException e) {
                // 실패 시 토스트 출력
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        Toast.makeText(InfoActivity.this, getString(R.string.toast_failed),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            finally {
                progressDialog.dismiss();
            }
        }
    }
}

package kr.hee.kwnoti.u_campus_activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import kr.hee.kwnoti.BrowserActivity;
import kr.hee.kwnoti.KEY;
import kr.hee.kwnoti.R;
import kr.hee.kwnoti.UTILS;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class UCampusListActivity extends Activity {
    RecyclerView recyclerView;
    UCampusListAdapter adapter;
    LinearLayoutManager layoutManager;

    UCamConnectionInterface request;

    ProgressDialog progressDialog;

    private String listType;

    TextView textView;

    private int currPage = 1;



    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ucam_lectures);

        initView();

        progressDialog.show();


        // 전달받은 데이터 추출
        UCampusMainData parcel = (UCampusMainData)(getIntent().getExtras().get(KEY.SUBJECT));
        listType = getIntent().getExtras().getString(KEY.SUBJECT_LOAD_TYPE);

        setTitle(parcel.subjName + " : " + listType);

        // 강의 계획서인 경우
        if (listType.equals(KEY.SUBJECT_PLAN)) {
            Intent intent = new Intent(this, BrowserActivity.class);
            intent.putExtra(KEY.BROWSER_TITLE, parcel.subjName + " : 강의 계획서");
            intent.putExtra(KEY.BROWSER_FROM_LECTURE_NOTE, true);
            intent.putExtra(KEY.BROWSER_DATA, parcel.subjId); // 강의계획서의 경우 아이디를 데이터로 보냄

            startActivity(intent);
            finish();
        }
        else {
            request = UCamConnection.getInstance().getUCamInterface(UCampusListActivity.this);

            new ParserThread(parcel).start();
        }
    }

    void initView() {
        recyclerView = (RecyclerView)findViewById(R.id.ucampusList_recyclerView);
        recyclerView.setLayoutManager(layoutManager = new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter = new UCampusListAdapter(this));
        recyclerView.addOnScrollListener(endlessScrollListener);

        textView = (TextView)findViewById(R.id.ucampusList_alert);

        // 다이얼로그 생성
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.dialog_loading));
    }

    class ParserThread extends Thread {
        private UCampusMainData subjectData;
        String url;
        ParserThread(UCampusMainData subjectData) {
            this.subjectData = subjectData;
        }

        @Override public void run() {
            super.run();

            // 스위치
            switch (listType)
            {
                case KEY.SUBJECT_INFO : // 공지사항
                    url = "http://info2.kw.ac.kr/servlet/controller.learn.NoticeStuServlet"; break;
                case KEY.SUBJECT_UTIL : // 강의 자료실
                    url = "http://info2.kw.ac.kr/servlet/controller.learn.AssPdsStuServlet"; break;
                case KEY.SUBJECT_STUDENT : // 학생 자료실
                    url = "http://info2.kw.ac.kr/servlet/controller.learn.PdsProfServlet"; break;
                case KEY.SUBJECT_ASSIGNMENT : // 과제 제출
                    url = "http://info2.kw.ac.kr/servlet/controller.learn.ReportStuServlet"; break;
                case KEY.SUBJECT_QNA :
                    url = "http://info2.kw.ac.kr/servlet/controller.learn.QnAStuServlet"; break;
                default :
                    url = "about:blank"; break;
            }

            // 유캠퍼스 연결 및 데이터 추출
            Call<ResponseBody> call = request.getList(url, "listPage", "N000003",
                    subjectData.subjId, subjectData.subjYear, subjectData.subjTerm, subjectData.subjClass, currPage);
            try {
                Response<ResponseBody> response = call.execute();
                String responseHtml = response.body().string();
                if (!parseHtml(responseHtml)) // 파서로 HTML 데이터를 넘김
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            textView.setVisibility(View.VISIBLE); // 데이터가 없으면 없다는 메세지 출력
                        }
                    });
            }
            catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        UTILS.showToast(UCampusListActivity.this, "오류가 발생했습니다.");
                    }
                });
                e.printStackTrace();
            }
            finally {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        progressDialog.dismiss();
                    }
                });
            }
        }

        boolean parseHtml(String html) {
            Document doc = Jsoup.parse(html);
            Elements elements_title = doc.select("td.tl_l");    // 항목명
            Elements elements_content = doc.select("td.tl_c");  // 부가사항

            // 데이터가 없는지 검사. 만약 마지막 페이지에서 데이터가 없다면 경고 메세지 표기 안함
            if (elements_title.size() < 1)
                return currPage != 1;

            Element title;
            Element content;
            UCampusListData data;
            for (int i_t = 0, i_c = 1;;) {
                data = new UCampusListData();
                // 항목명
                title = elements_title.get(i_t++);
                data.title = title.text();
                data.id = title.html().split("\'")[1];
                data.url = url;
                // 번호
                content = elements_content.get(i_c++);
                data.number = content.text();
                // 강의자료실에 한해 순서를 바꿈
                if (listType.equals(KEY.SUBJECT_UTIL)) {
                    // 작성일
                    content = elements_content.get(i_c++);
                    data.date = content.text();
                    // 첨부파일 유무
                    content = elements_content.get(i_c++);
                    data.attachment = content.hasText();
                }
                else {
                    // 첨부파일 유무
                    content = elements_content.get(i_c++);
                    data.attachment = content.hasText();
                    // 작성일
                    content = elements_content.get(i_c++);
                    data.date = content.text();
                    // 작성자
                    content = elements_content.get(i_c++);
                    data.writer = content.text();
                }
                // 조회수
                content = elements_content.get(i_c++);
                data.views = content.text();
                i_c++;

                adapter.addData(data);

                if (i_t >= elements_title.size() || i_c >= elements_content.size()) break;
            }
            // 어댑터
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
            return true;
        }
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
                        <= (firstVisibleItem + visibleThreshold)) {
                    // 다음 쪽 로딩
                    currPage++;
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            progressDialog.show();
                        }
                    });
                    new ParserThread((UCampusMainData)(getIntent().getExtras().get(KEY.SUBJECT))).start();
                    loading = true;
                }
            }
        }
    };

    @Override protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }
}

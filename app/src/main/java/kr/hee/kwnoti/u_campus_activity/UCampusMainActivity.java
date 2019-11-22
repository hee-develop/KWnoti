package kr.hee.kwnoti.u_campus_activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import kr.hee.kwnoti.ActivityLoadingBase;
import kr.hee.kwnoti.R;
import kr.hee.kwnoti.UTILS;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class UCampusMainActivity extends ActivityLoadingBase {
    // 리사이클러 뷰 친구들
    RecyclerView recyclerView;
    UCampusMainAdapter adapter;
    // 하단의 강의계획서 검색 버튼
    Button button;

    // 로그인을 위한 데이터와 Retrofit 인터페이스
    private String stuId;
    private String stuPwd;
    UCamConnectionInterface request;

    // 로그인 스레드
    LoginThread loginThread;
    final static String THREAD_NAME1 = "LoginThread";
    final static String THREAD_NAME2 = "UCamThread";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ucam_main);
        setTitle(R.string.uCam_title);

        SharedPreferences pref = UTILS.checkUserData(this);
        if (pref == null)   finish();
        else {
            initView();
            init(pref);
            loadStart();
        }
    }

    /** 액티비티 데이터 초기화 및 데이터 불러오기 메소드 */
    void init(SharedPreferences pref) {
        // 유저 데이터 불러오기
        stuId   = pref.getString(getString(R.string.key_stuID), "");
        stuPwd  = pref.getString(getString(R.string.key_stuUCampusPwd), "");

        // 로그인 시도
        request = UCamConnection.getInstance().getUCamInterface(this);
        loginThread = new LoginThread();
        loginThread.start();
        // 유캠퍼스 접속 시도
        new GetUcampusThread().start();
    }

    /** 리사이클러 뷰 초기화 및 다이얼로그 생성 메소드 */
    void initView() {
        // 리사이클러 뷰 설정
        recyclerView = (RecyclerView)findViewById(R.id.uCam_main_recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2열 그리드
        recyclerView.setAdapter(adapter = new UCampusMainAdapter(this));

        // 하단 버튼 설정
        button = (Button)findViewById(R.id.uCam_main_btn_search);
        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                startActivity(new Intent(UCampusMainActivity.this,
                        LectureSearchActivity.class));
            }
        });
    }

    /** 유캠퍼스 로그인을 위한 Thread 객체 */
    private class LoginThread extends Thread {
        @Override public void run() {
            super.run();

            // 스레드 이름 설정
            setName(THREAD_NAME1);

            // 로그인 요청 ===========================================================================
            Call<ResponseBody> call = request.getCookie("2", "http%3A%2F%2Finfo.kw.ac.kr%2F", "11",
                    "", "KOREAN", stuId, stuPwd);
            try {
                Response<ResponseBody> response = call.execute();
                String responseHtml = new String(response.body().bytes(), "EUC-kR");
                if (responseHtml.contains("비밀번호가 맞지 않습니다") || responseHtml.contains("비밀번호를 입력하세요"))
                    throw new IOException();
            }
            catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        UTILS.showToast(UCampusMainActivity.this, "로그인에 실패했습니다.");
                    }
                });
                e.printStackTrace();
                return;
            }

            // 유캠퍼스 로드 요청 =====================================================================
            call = request.getUcampus();
            try {
                call.execute();
            }
            catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        UTILS.showToast(UCampusMainActivity.this, "오류가 발생했습니다.");
                    }
                });
                e.printStackTrace();
            }
        }
    }

    /** 유캠퍼스 로그인 이후 메인 화면을 얻어오기 위한 Thread 객체 */
    class GetUcampusThread extends Thread {
        @Override public void run() {
            super.run();

            // 스레드 이름 설정
            setName(THREAD_NAME2);


            // 로그인 스레드가 끝나야 실행
            try {
                loginThread.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 유캠퍼스 데이터 로드 요청 ===============================================================
            Call<ResponseBody> call = request.getUcampusCore(/*"", "univ", "N000003", "U2016209697220013", "2016", "2", "01", "2014722028UA", "", "11", ""*/);
            try {
                Response<ResponseBody> response = call.execute();
                String responseHtml;
                if (response.body() == null)
                    responseHtml = "";
                else responseHtml = response.body().string();
                // 로그인 이후 나온 데이터를 파싱하여 RecyclerView 데이터로 넣어줌
                setViewData(responseHtml);
            }
            catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        UTILS.showToast(UCampusMainActivity.this, "오류가 발생했습니다.");
                    }
                });
                e.printStackTrace();
            }
            finally {
                loadFinish();
            }
        }

        // 파싱된 데이터를 RecyclerView 데이터로 넣는 메소드
        void setViewData(String html) throws IOException {
            Document doc = Jsoup.parse(html);
            if (doc.text().equals(""))
                throw new IOException("Blank body.");

            // 수강 과목 및 데이터 추출
            Elements elements = doc.select("table.main_box").last().select("td.list_txt");

            // 과목 별 데이터 추출
            UCampusMainData data = null;
            Element element = elements.first();
            for (int i = 0; i < elements.size();) {
                data = new UCampusMainData();
                // 과목명
                data.subjName = element.text()
                        .replace("[학부]", "")        // [학부] 제거
                        .replaceAll(" \\(..\\)", "");// (01) 제거
                element = elements.get(++i);
                // 강의실
                data.subjPlace = element.text();
                element = elements.get(++i);
                // 강의번호
                String[] subjectData = element.html().split("\'");
                // SELC 인강인 경우 값을 다르게 설정해줘야 함
                if (subjectData.length == 1) {
                    data.subjId     = "";
                    data.subjYear   = "";
                    data.subjTerm   = "";
                    data.subjClass  = "";
                }
                else {
                    data.subjId = subjectData[1];
                    data.subjYear = subjectData[3];
                    data.subjTerm = subjectData[5];
                    data.subjClass = subjectData[7];
                }
                // 신규 과제 혹은 공지사항 여부 확인
                if (subjectData.length > 9) {
                    String newSomething = subjectData[16];
                    String newAssignment = null;
                    if (subjectData.length > 17) newAssignment = subjectData[24];

                    // 신규 공지사항
                    if (newSomething.contains("btn_n.gif")) data.newNotice = true;
                    // 신규 과제
                    else if (newSomething.contains("btn_w.gif")) data.newAssignment = true;

                    if (newAssignment != null) data.newAssignment = true;
                }

                // 데이터 추가
                adapter.addData(data);
                // 다음 공지 유무 확인 및 파싱 종료
                if (element != elements.last()) element = elements.get(++i);
                else                            break;
            }

            // 데이터 로드가 모두 끝나면 어댑터에 알리고 다이얼로그를 없앰
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    /** 로딩화면을 중간에 멈추면 스레드 제거 */
    @Override public void loadCanceled() {
        // 현재 동작중인 ParserThread 검색
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        while (true) {
            ThreadGroup tg2 = tg.getParent();
            if (tg2 != null) tg = tg2;
            else break;
        }
        Thread[] threads = new Thread[64];
        tg.enumerate(threads); // 현재 스레드를 배열에 삽입

        // 스레드 동작 중지 요청. interrupt()를 한다고 해도 바로 종료되지는 않기 때문에 flag 추가 설정
        for (Thread thread : threads) {
            if (thread != null) {
                final String threadName = thread.getName();

                if (threadName.equals(THREAD_NAME1) ||
                        threadName.equals(THREAD_NAME2))
                    thread.interrupt();
            }
        }
    }
}
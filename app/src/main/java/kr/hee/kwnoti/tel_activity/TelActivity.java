package kr.hee.kwnoti.tel_activity;

import android.database.sqlite.SQLiteDatabaseLockedException;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import kr.hee.kwnoti.ActivityLoadingBase;
import kr.hee.kwnoti.R;
import kr.hee.kwnoti.UTILS;

/** 교내 전화번호 액티비티 */
public class TelActivity extends ActivityLoadingBase {
    RecyclerView    recyclerView;
    TelAdapter      adapter;
    EditText        editText;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tel);
        setTitle(R.string.tel_title);
        // 뷰 초기화 및 다이얼로그 설정
        initView();

        // 어댑터가 비어 있으면 학사일정 새로 불러오기
        if (adapter.getItemCount() == 0)
            new ParserThread().start();
    }

    /** 뷰 초기화 메소드 */
    void initView() {
        recyclerView = (RecyclerView)findViewById(R.id.tel_recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter = new TelAdapter(TelActivity.this));

        editText = (EditText)findViewById(R.id.tel_editText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_SEARCH) {
                    adapter.findData(editText.getText().toString());
                }
                return true;
            }
        });
    }

    // 메뉴 버튼 인플레이트
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tel, menu);
        return true;
    }
    // 메뉴 버튼 클릭 이벤트 리스너 - 새로고침 하나밖에 없으므로 별도의 switch 필요 없음
    @Override public boolean onMenuItemSelected(int clickId, final MenuItem item) {
        new ParserThread().start();
        return true;
    }


    /* ================================= 스레드 ================================= */
    private static final String THREAD_NAME = "TelThread";

    /** Jsoup을 이용한 파서 스레드 */
    private final class ParserThread extends Thread {
        // 스레드를 멈추는지 여부를 판단할 플래그
        boolean stopLoop;

        @Override public void run() {
            super.run();
            // 플래그 및 스레드 이름 설정
            stopLoop = false;
            setName(THREAD_NAME);

            // 로딩 다이얼로그 표시
            loadStart();


            TelDB db = new TelDB(TelActivity.this);

            String url = "http://info.kw.ac.kr/webnote/phonebook/phonebook_fuz.php";
            try {
                Document doc = Jsoup.connect(url).timeout(5000).get();

                // 어댑터 내 모든 데이터 삭제
                adapter.cleanData();

                Elements elements = doc.select("dl dt, dd li");

                String groupName = null;
                for (Element element : elements) {
                    // 멈추라는 신호가 있는 경우 중단
                    if (stopLoop) return;

                    String[] content = element.toString().split(">|<");
                    // 제목인 경우
                    if (content[1].contains("dt")) {
                        groupName = content[4];
                    }
                    else {
                        // 전화번호에 지역국번과 940 추가
                        StringBuilder telNumber = new StringBuilder(content[4]);
                        if (telNumber.indexOf("-") == -1)   telNumber.insert(0, "02-940-");
                        else                                telNumber.insert(0, "02-");

                        TelData telData = new TelData(groupName, content[2], telNumber.toString());
                        db.addTel(telData);
                    }
                }

                // 멈추라는 신호가 있는 경우 중단
                if (stopLoop) return;

                // 어댑터에 데이터 적용 및 새로고침 알림
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        recyclerView.setAdapter(adapter = new TelAdapter(TelActivity.this));
                        UTILS.showToast(TelActivity.this, getString(R.string.toast_refreshed));
                    }
                });
            }
            catch (IOException e) {
                // 파싱 실패 시 토스트 출력
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        UTILS.showToast(TelActivity.this, getString(R.string.toast_failed));
                    }
                });
            }
            catch (SQLiteDatabaseLockedException e) {
                // 너무 많은 클릭에 대한 토스트 출력
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        UTILS.showToast(TelActivity.this, "오류입니다. 다시 시도해주세요.");
                    }
                });
            }
            finally {
                loadFinish();
            }
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
            if (thread != null && thread.getName().equals(THREAD_NAME)) {
                // 스레드 중지 요청
                thread.interrupt();
                // 스레드 내 반복문 강제 중지
                ((ParserThread)thread).stopLoop = true;

                break;
            }
        }
    }
}

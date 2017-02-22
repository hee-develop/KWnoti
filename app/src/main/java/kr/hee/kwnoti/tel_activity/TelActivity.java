package kr.hee.kwnoti.tel_activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import kr.hee.kwnoti.R;
import kr.hee.kwnoti.UTILS;

/** 교내 전화번호 액티비티 */
public class TelActivity extends Activity {
    RecyclerView    recyclerView;
    TelAdapter      adapter;
    EditText        editText;
    ProgressDialog progressDialog;

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

        // 로딩 다이얼로그 설정
        progressDialog = new ProgressDialog(TelActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.dialog_loading));
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

    /** Jsoup을 이용한 파서 스레드 */
    class ParserThread extends Thread {
        @Override public void run() {
            super.run();
            // 로딩 중 다이얼로그 표시
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    progressDialog.show();
                }
            });

            TelDB db = new TelDB(TelActivity.this);

            String url = "http://info.kw.ac.kr/webnote/phonebook/phonebook_fuz.php";
            try {
                Document doc = Jsoup.connect(url).timeout(5000).get();

                // 어댑터 내 모든 데이터 삭제
                adapter.cleanData();

                Elements elements = doc.select("dl dt, dd li");

                String groupName = null;
                for (Element element : elements) {
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
            finally {
                progressDialog.dismiss();
            }
        }
    }

    // 다이얼로그 메모리 유출 방지
    @Override protected void onDestroy() {
        super.onDestroy();
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}

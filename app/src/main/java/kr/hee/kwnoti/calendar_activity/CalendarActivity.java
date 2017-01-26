package kr.hee.kwnoti.calendar_activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import kr.hee.kwnoti.R;
import kr.hee.kwnoti.UTILS;

/** 학사 일정 액티비티 */
public class CalendarActivity extends Activity {
    Toolbar toolbar;
    ImageView btn_refresh;
    RecyclerView recyclerView;
    CalendarAdapter adapter;
    ProgressDialog progressDialog;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        toolbar      = (Toolbar)findViewById(R.id.calendar_toolbar);
        btn_refresh  = (ImageView)findViewById(R.id.calendar_toolbar_refresh);
        recyclerView = (RecyclerView)findViewById(R.id.calendar_recyclerView);
        setActionBar(toolbar);
        // 새로고침 버튼을 누르면 데이터 삭제 후 새로 불러오기
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                new ParserThread().start();
            }
        });

        // 다이얼로그 모양 설정
        progressDialog = new ProgressDialog(this, android.R.style.Theme_Material_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.dialog_loading));

        adapter = new CalendarAdapter(getApplicationContext());
        // 데이터가 없으면 새로 불러오기
        if (adapter.getItemCount() == 0)
            new ParserThread().start();

        // 어댑터 연결 및 레이아웃 설정
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    /** Jsoup을 이용한 파서 스레드, InfoActivity 파서와 방식이 달라서 따로 만들었음. */
    class ParserThread extends Thread {
        @Override public void run() {
            super.run();
            // 로딩 중 다이얼로그 표시
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    progressDialog.show();
                }
            });

            CalendarDB db = new CalendarDB(getApplicationContext());

            String url = "http://www.kw.ac.kr/ko/life/bachelor_calendar.do";
            try {
                Document doc = Jsoup.connect(url).timeout(5000).get();
                // 최초 형태는 JSON Object({}) 형태임
                JSONObject bachelorJSON = new JSONObject(doc.select("textarea").text());

                // 어댑터의 모든 데이터를 삭제
                adapter.cleanData();

                for (int month = 1; month <= bachelorJSON.length(); month++) {
                    // JSON Array([]) 형태를 가져 옴
                    String monthData = bachelorJSON.get("bachelor_" + month).toString();
                    // 배열이긴 하나 JSON Object 딸랑 하나 들어있어서 배열의 의미가 없음. 정규식을 통해 배열 해제
                    monthData = monthData.replaceAll("\\[|\\]", "");
                    // 배열을 해제하면 JSON Object 형태만 남으므로, 바로 대입 가능.
                    JSONObject realData = new JSONObject(monthData);

                    int objectSize = Integer.parseInt(realData.getString("size"));
                    for (int number = 0; number < objectSize; number++) {
                        // 시작일은 sd_달_번호, 종료일은 ed_달_번호
                        String  startDay = realData.getString("sd_" + month + "_" + number),
                                endDay   = realData.getString("ed_" + month + "_" + number),
                                content  = realData.getString("con_" + month + "_" + number);

                        String[] startDays  = startDay.split("\\-");
                        String[] endDays    = endDay.split("\\-");

                        // 형변환 오류 수정을 위한 띄어쓰기 제거
                        startDays[0] = startDays[0].replaceAll(" ", "");
                        startDays[1] = startDays[1].replaceAll(" ", "");
                        startDays[2] = startDays[2].replaceAll(" ", "");
                        if (endDay.length() != 0) {
                            endDays[0] = endDays[0].replaceAll(" ", "");
                            endDays[1] = endDays[1].replaceAll(" ", "");
                            endDays[2] = endDays[2].replaceAll(" ", "");
                        }

                        // DB에 데이터 추가
                        CalendarData data;
                        if (endDay.length() == 0)
                            data = new CalendarData(startDays[0], startDays[1], startDays[2], content);
                        else
                            data = new CalendarData(startDays[0], startDays[1], startDays[2],
                                    endDays[1], endDays[2], content);
                        db.addCalendar(data);
                    }
                }
                // 파싱 성공 시 토스트 출력
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        recyclerView.setAdapter(new CalendarAdapter(getApplicationContext()));
                        Toast.makeText(CalendarActivity.this, getString(R.string.toast_refreshed),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch (IOException | JSONException e) {
                // 파싱 실패 시 토스트 출력
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        Toast.makeText(CalendarActivity.this, getString(R.string.toast_failed),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            finally {
                // 파싱이 종료되면 다이얼로그 없앰
                progressDialog.dismiss();
            }
        }
    }
}

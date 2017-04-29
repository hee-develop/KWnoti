package kr.hee.kwnoti.calendar_activity;

import android.os.Bundle;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import kr.hee.kwnoti.R;
import kr.hee.kwnoti.RecyclerViewActivity;
import kr.hee.kwnoti.UTILS;

/** 학사 일정 액티비티 */
public class CalendarActivity extends RecyclerViewActivity {
    CalendarAdapter adapter;

    // 액티비티 초기화 및 데이터 설정
    public CalendarActivity() {
        super(R.layout.activity_calendar,   // 액티비티 레이아웃
              R.menu.menu_calendar,         // 액티비티 메뉴
              R.id.calendar_recyclerView);  // 리사이클러 뷰 ID
    }

    /** 타이틀 및 어댑터 설정 */
    @Override public void setTitleAndAdapter() {
        setTitle(R.string.calendar_title);
        recyclerView.setAdapter(adapter = new CalendarAdapter(this));
    }

    /** 학사 일정 불러오기 및 리스트 위치 자동 이동 */
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 어댑터가 비어 있으면 학사일정 새로 불러오기
        if (adapter.getItemCount() == 0)
            new CalendarParserThread().start();

        // 오늘의 날짜에 맞는 학사 일정으로 자동 이동
        recyclerView.post(new Runnable() {
            @Override public void run() {
                layoutManager.
                        scrollToPositionWithOffset(adapter.getTodayPosition(), 0);
            }
        });
    }

    /** 메뉴 버튼 클릭 리스너 설정 */
    @Override public boolean onMenuItemSelected(int clickId, final MenuItem item) {
        new CalendarParserThread().start();
        return true;
    }

    /** Jsoup을 이용한 파서 스레드 TODO 성능 최적화 */
    private class CalendarParserThread extends Thread {
        @Override public void run() {
            super.run();
            // 로딩 중 다이얼로그 표시
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    progressDialog.show();
                }
            });

            // 학사 일정 데이터를 저장할 클래스. DB 파일을 직접적으로 다룸
            CalendarDB db = new CalendarDB(CalendarActivity.this);

            String url = "http://www.kw.ac.kr/ko/life/bachelor_calendar.do";
            try {
                Document doc = Jsoup.connect(url).timeout(5000).get();
                // 최초 형태는 JSON Object({}) 형태임
                JSONObject bachelorJSON = new JSONObject(doc.select("textarea").text());

                // 어댑터의 모든 데이터를 삭제
                adapter.cleanData(CalendarActivity.this);

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
                        content = content.replaceAll("\\n", "");

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
                        db.addToDB(data);
                    }
                }
                // 파싱 성공 시 토스트 출력
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        recyclerView.setAdapter(new CalendarAdapter(CalendarActivity.this));
                        UTILS.showToast(CalendarActivity.this, getString(R.string.toast_refreshed));
                    }
                });
            }
            catch (IOException | JSONException e) {
                // 파싱 실패 시 토스트 출력
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        UTILS.showToast(CalendarActivity.this, getString(R.string.toast_failed));
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

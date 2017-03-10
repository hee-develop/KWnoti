package kr.hee.kwnoti.food_activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import kr.hee.kwnoti.R;
import kr.hee.kwnoti.UTILS;

/** 금주의 학식 */
public class FoodActivity extends Activity {
    TextView textView;  // 지금 시간대의 식단
    RecyclerView recyclerView;
    FoodAdapter adapter;
    ProgressDialog progressDialog;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        setTitle(R.string.food_title);

        initView();

        // 학식 데이터 TODO 휴대폰 기준 날짜로 확인
        new ParserThread().start();
    }

    void initView() {
        // 리사이클러 뷰 설정
        recyclerView= (RecyclerView)findViewById(R.id.food_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter = new FoodAdapter(this));

        // 오늘의 학식 데이터 설정
        textView    = (TextView)findViewById(R.id.food_today);
        textView.setText("오늘의 학식은\n" + "" + "\n입니다.");

        // 다이얼로그 모양 설정
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.dialog_loading));
    }

    // 다이얼로그 메모리 유출 방지
    @Override protected void onDestroy() {
        super.onDestroy();
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    /** Jsoup을 이용한 파서 스레드 */
    private class ParserThread extends Thread {
        @Override public void run() {
            super.run();
            // 로딩 중 다이얼로그 표시
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    progressDialog.show();
                }
            });

            // 학식 데이터를 다루는 DB 클래스 생성
            FoodDB db = new FoodDB(FoodActivity.this);

            String url = "http://www.kw.ac.kr/ko/life/facility11.do";
            try {
                Document doc = Jsoup.connect(url).timeout(5000).get();

                // 학식 조회 기간 설정
                String foodDate = doc.select("div input[id=endPeriodTime").val();
                // TODO SharedPRef에 데이터 추가

                // 학식을 JSON 형식으로 가져 옴
                JSONObject foodArray = new JSONObject(doc.select("textarea").text());

                int length = (Integer)foodArray.get("dietLength");
                for (int i = 0; i < length; i++) {
                    
                }


                // 어댑터의 모든 데이터를 삭제
                adapter.cleanData();

                /*for (int month = 1; month <= bachelorJSON.length(); month++) {
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
                        FoodData data;
                        if (endDay.length() == 0)
                            data = new FoodData(startDays[0], startDays[1], startDays[2], content);
                        else
                            data = new FoodData(startDays[0], startDays[1], startDays[2],
                                    endDays[1], endDays[2], content);
                        db.addCalendar(data);
                    }
                }
                // 파싱 성공 시 토스트 출력
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        recyclerView.setAdapter(new FoodAdapter(CalendarActivity.this));
                        UTILS.showToast(CalendarActivity.this, getString(R.string.toast_refreshed));
                    }
                });*/
            }
            catch (IOException e) {
                // 파싱 실패 시 토스트 출력
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        UTILS.showToast(FoodActivity.this, getString(R.string.toast_failed));
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                // 파싱이 종료되면 다이얼로그 없앰
                progressDialog.dismiss();
            }
        }
    }
}

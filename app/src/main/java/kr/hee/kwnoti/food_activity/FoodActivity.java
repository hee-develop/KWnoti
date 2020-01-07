package kr.hee.kwnoti.food_activity;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

import kr.hee.kwnoti.LoadingActivity;
import kr.hee.kwnoti.R;
import kr.hee.kwnoti.info_activity.DataReceived;
import kr.hee.kwnoti.RequestThread;

public class FoodActivity extends LoadingActivity implements DataReceived {
    ArrayList<FoodData> foodArray = new ArrayList<>();
    final String URL = "https://www.kw.ac.kr/ko/life/facility11.do";

    RecyclerView recyclerView;
    FoodAdapter adapter;

    @Override
    protected void onStart() {
        super.onStart();

        new RequestFoodThread(URL).start();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_food);
        recyclerView = findViewById(R.id.food_recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter = new FoodAdapter(this, foodArray));
    }

    @Override
    public void setDialog() {
        Dialog d = new Dialog(this);
        d.setTitle("A");
        loadingDialog = d;
    }


    public class RequestFoodThread extends RequestThread {
        RequestFoodThread(String url) {
            super(url);
        }

        @Override
        public void afterReceived(Document doc) {
            ArrayList<FoodData> foodList = new ArrayList<>();
            JSONObject foodJson = null;
            String foodStartDate, foodEndDate;
            int foodLength = 0;

            String[] foodTitle;
            String[] foodPrice;
            String[] foodTime;
            String[][] foodContents;

            if (doc == null) return;


            // get data
            try {
                foodStartDate = doc.select("#startPeriodTime").text();
                foodEndDate   = doc.select("#endPeriodTime").text();

                foodJson = new JSONObject(doc.select("div.diet>textarea[name=articleText]")
                        .first().text());

                if (foodJson.optString("dietLength").length() != 0) {
                    foodLength = foodJson.getInt("dietLength");
                }

                // initialize array
                foodTitle = new String[foodLength];
                foodPrice = new String[foodLength];
                foodTime  = new String[foodLength];
                foodContents = new String[foodLength][7];

                for (int i=0; i<foodLength; i++) {
                    String curPtr = "diet_" + i;

                    if (foodJson.optString(curPtr).length() == 0) continue;

                    JSONObject curData = foodJson.getJSONArray(curPtr).getJSONObject(0);

                    foodTitle[i] = curData.getString("ti");
                    foodPrice[i] = curData.getString("p");
                    foodTime[i]  = curData.getString("st") + " ~ " + curData.getString("et");

                    for (int j=0; j<7; j++) {
                        String curContentPtr = "d" + (j+1);
                        if (curData.optString(curContentPtr).length() == 0) break;
                        foodContents[i][j] = curData.getString(curContentPtr);
                    }

                    foodList.add(new FoodData(foodTitle[i], foodPrice[i], foodTime[i], foodContents[i]));
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            // set view
            runOnUiThread(() -> {
                LinearLayout layout = findViewById(R.id.food_layout_restaurant);
//                layout.addView(new TextView(getApplicationContext()?));
            });

            makeView(foodList);
        }

        @Override
        public void catchError() {
            /*TODO*/
        }
    }


    @Override
    public void makeView(ArrayList arr) {
        for (Object i : arr) {
            foodArray.add((FoodData)i);
        }

        runOnUiThread(() -> {
            this.onLoadFinished();
            adapter.notifyDataSetChanged();
        });
    }
}



/** 금주의 학식 */


/*

public class FoodActivity extends AppCompatActivity {
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

    */
/** Jsoup을 이용한 파서 스레드 *//*

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
                String foodDate = doc.select("div input[id=endPeriodTime]").val();

                // 학식 유효기간 확인
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    if (!pref.getString(KEY.FOOD_DATE, "").equals(foodDate)) {
                }
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(KEY.FOOD_DATE, foodDate).apply();
                // TODO SharedPRef에 데이터 추가

                // 어댑터의 모든 데이터를 삭제
                adapter.cleanData();

                // 학식을 JSON 형식으로 가져 옴
                JSONObject foodArray = new JSONObject(doc.select("div.ko > textarea").text());

                // JSON 데이터 추출
                int length = (Integer)foodArray.get("dietLength");

                for (int i = 0; i < length; i++) {
                    JSONObject foodObject = foodArray.getJSONArray("diet_" + i).getJSONObject(0);

                    // 조식, 중식, 석식 구분
                    String type = (String)foodObject.get("ti");
                    // 학식 운영시간
                    String time = (String)foodObject.get("st");
                    String endTime   = (String)foodObject.get("et");
                    // 가격
                    String price     = (String)foodObject.get("p");
                    // 학식 내용
                    String[] foods   = new String[] {
                            (String) foodObject.get("d1"),
                            (String) foodObject.get("d2"),
                            (String) foodObject.get("d3"),
                            (String) foodObject.get("d4"),
                            (String) foodObject.get("d5")
                    };

                    // DB에 학식 데이터 추가
                    FoodData data = new FoodData(type, price, time, endTime, foods);
                    db.addToDB(data);
                }

                // 파싱 성공 시 토스트 출력
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        recyclerView.setAdapter(new FoodAdapter(FoodActivity.this));
                        UTILS.showToast(FoodActivity.this, getString(R.string.toast_refreshed));
                    }
                });
            }
            catch (IOException e) {
                // 파싱 실패 시 토스트 출력
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        UTILS.showToast(FoodActivity.this, getString(R.string.toast_failed));
                    }
                });
            }
            catch (JSONException e) {
                // 데이터 캐스팅 중 오류 발생 시 토스트 출력
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        UTILS.showToast(FoodActivity.this, "데이터를 얻어오는 데 문제가 발생했습니다.");
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
*/

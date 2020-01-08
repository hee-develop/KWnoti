package kr.hee.kwnoti.schedule_activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.hee.kwnoti.R;
import kr.hee.kwnoti.RequestThread;

import android.database.SQLException;
import android.os.Bundle;
import android.os.PatternMatcher;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScheduleActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ScheduleAdapter adapter;
    GridLayoutManager layoutManager;

    ScheduleDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        recyclerView = findViewById(R.id.schedule_recycler);
        recyclerView.setAdapter(adapter = new ScheduleAdapter(this));
        recyclerView.setLayoutManager(layoutManager = new GridLayoutManager(this, 7));

        database = new ScheduleDatabase(ScheduleActivity.this, null, 1);

        // request thread for parse data
        RequestThread requestThread = new RequestThread("http://www.kw.ac.kr/ko/life/bachelor_calendar.do") {
            @Override
            public void afterReceived(Document doc) {
                JSONObject bachelorJson = null;

                try {
                    bachelorJson = new JSONObject(doc.select("textarea").text());

                    for (int month=1; month<=bachelorJson.length(); month++) {
                        String monthData = null;
                        JSONObject jsonObject = null;

                        monthData = bachelorJson.get("bachelor_"+month).toString();
                        monthData = monthData.replaceAll("[\\[\\]]", "");

                        jsonObject = new JSONObject(monthData);

                        final int size = Integer.parseInt(jsonObject.getString("size"));
                        for (int i=0; i<size; i++) {
                            String[] startDay = jsonObject.getString("sd_"+month+"_"+i).replaceAll("[^\\d-]", "").split("-");
                            String[] endDay = jsonObject.getString("ed_"+month+"_"+i).replaceAll("[^\\d-]", "").split("-");
                            String content = jsonObject.getString("con_"+month+"_"+i);

                            ScheduleData data;
                            if (!endDay[0].equals(""))
                                data = new ScheduleData(startDay, endDay, content);
                            else
                                data = new ScheduleData(startDay, startDay, content);

                            if (!database.insertData(data))
                                throw new SQLException();
                        }
                    }
                }
                catch (JSONException e) {
                    /////////////
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void catchError() {
                runOnUiThread(() -> {
                    Snackbar.make(recyclerView/*TODO*/, "데이터를 받아오는 데 문제가 발생했습니다.", Snackbar.LENGTH_SHORT).show();
                });
            }
        };
        requestThread.start();

        try {
            requestThread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

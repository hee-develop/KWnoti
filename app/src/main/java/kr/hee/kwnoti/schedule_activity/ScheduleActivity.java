package kr.hee.kwnoti.schedule_activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.hee.kwnoti.R;

import android.os.Bundle;

public class ScheduleActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ScheduleAdapter adapter;
    GridLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        recyclerView = findViewById(R.id.schedule_recycler);
        recyclerView.setAdapter(adapter = new ScheduleAdapter(this));
        recyclerView.setLayoutManager(layoutManager = new GridLayoutManager(this, 7));
    }
}

package kr.hee.kwnoti.settings_activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import kr.hee.kwnoti.R;

/** 푸쉬 알림에서 차단/수신설정 할 데이터를 넣어주는 액티비티 */
public class PushFilterActivity extends AppCompatActivity {
    EditText edit_addFilter;
    RecyclerView filters;
    PushFilterAdapter adapter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_push_filter);
        edit_addFilter  = (EditText)findViewById(R.id.filter_editText);
        filters         = (RecyclerView)findViewById(R.id.filter_recyclerView);
        adapter         = new PushFilterAdapter(PushFilterActivity.this);

        filters.setAdapter(adapter);
        filters.setLayoutManager(new StaggeredGridLayoutManager(10,
                StaggeredGridLayoutManager.HORIZONTAL));
        edit_addFilter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    String text = textView.getText().toString();
                    // 정상적으로 추가가 되면 입력된 값을 지움
                    if (adapter.addFilter(text)) textView.setText("");
                    else return false;
                }
                // 키보드가 내려가지 않도록 설정해줌
                return true;
            }
        });
    }
}

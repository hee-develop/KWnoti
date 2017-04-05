package kr.hee.kwnoti;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

/** 리사이클러 뷰가 들어가 있는 액티비티의 뼈대
 * 로딩용 ProgressDialog 포함 */
public abstract class RecyclerViewActivity extends Activity {
    // 액티비티 내 리스트 & 레이아웃 매니저(일반적으로 LinearLayout 이므로)
    public RecyclerView recyclerView;
    public LinearLayoutManager layoutManager;
    // 로딩 다이얼로그
    public ProgressDialog progressDialog;

    // 액티비티의 ID
    int layoutId;       // R.layout.액티비티레이아웃
    int menuId;         // R.menu.액티비티메뉴
    int recyclerViewId; // R.id.리사이클러뷰

    /** 액티비티 초기화
     * @param layoutId          액티비티 레이아웃
     * @param menuId            액티비티 메뉴
     * @param recyclerViewId    액티비티의 리사이클러 뷰 */
    public RecyclerViewActivity(int layoutId, int menuId, int recyclerViewId) {
        this.layoutId = layoutId;
        this.menuId = menuId;
        this.recyclerViewId = recyclerViewId;
    }

    /** 리사이클러 뷰의 어댑터 설정 */
    public abstract void setTitleAndAdapter();

    /** 액티비티 생성 */
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 레이아웃 및 제목 설정
        setContentView(layoutId);

        // 뷰 초기화
        recyclerView = (RecyclerView)findViewById(recyclerViewId);
        recyclerView.setLayoutManager(layoutManager = new LinearLayoutManager(this));

        // 다이얼로그 모양 설정
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.dialog_loading));

        setTitleAndAdapter();
    }

    /** 액티비티 메뉴 인플레이트 */
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menuId, menu);
        return true;
    }

    /** 액티비티 메뉴 리스너 */
    public abstract boolean onMenuItemSelected(int clickId, final MenuItem menuItem);

    /** 액티비티 소멸 때 다이얼로그도 같이 소멸 */
    @Override protected void onDestroy() {
        super.onDestroy();
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}

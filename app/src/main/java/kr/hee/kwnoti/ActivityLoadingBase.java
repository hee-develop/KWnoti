package kr.hee.kwnoti;

import android.content.DialogInterface;

/** 로딩 다이얼로그가 필요한 액티비티 */
public abstract class ActivityLoadingBase extends ActivityBase {
    /** 로딩이 시작됐을 때 불리는 메소드 */
    public void loadStart() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                BaseApplication.getInstance().loadStart(ActivityLoadingBase.this);
                BaseApplication.getInstance().attachCancelListener(cancelListener);
            }
        });
    }

    /** 로딩이 정상적으로 끝났을 때 불리는 메소드 */
    public void loadFinish() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                BaseApplication.getInstance().loadFinish();
            }
        });
    }

    public DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {
        @Override public void onCancel(DialogInterface dialogInterface) {
            loadFinish();
            loadCanceled();

            UTILS.showToast(ActivityLoadingBase.this, "불러오기가 중단됐습니다.");
        }
    };

    /** 로딩이 중간에 취소됐을 때 불리는 메소드 */
    public abstract void loadCanceled();

    /** 액티비티가 종료될 때 다이얼로그로 인한 메모리 누수 방지 */
    @Override protected void onDestroy() {
        super.onDestroy();
        loadFinish();
    }
}

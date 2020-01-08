package kr.hee.kwnoti;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

/**
 * Activity class for using loading progress
 * Please insert loading xml in loading activity that you want to use.
 */
public abstract class LoadingActivity extends Activity {
    View mProgressLayout;
    TextView mProgressCancelBtn;

    Handler mHandler;

    /**
     * override onStart method
     * onStart method calls after onCreate, so
     * we can use findViewById method
     */
    @Override
    protected void onStart() {
        super.onStart();

        mProgressLayout = findViewById(R.id.loading_layout);
        mProgressCancelBtn = findViewById(R.id.loading_btn_stop);
        mProgressCancelBtn.setOnClickListener((v) -> onLoadCanceled());

        mHandler = new Handler();

        onLoadStart();
    }

    /**
     * use to view progress
     */
    private void onLoadStart() {
        mProgressLayout.setVisibility(View.VISIBLE);

        mHandler.postDelayed(() -> runOnUiThread(() -> {
            if (mProgressCancelBtn != null) mProgressCancelBtn.setVisibility(View.VISIBLE);
        }), 2000);
    }

    /**
     * use to hide progress
     * call onLoadFinishedCallback()
     */
    private void onLoadFinished() {
        mProgressLayout.setVisibility(View.GONE);
        onLoadFinishedCallback();
    }

    /**
     * use to hide progress
     * show bar to notify load failed
     */
    private void onLoadCanceled() {
        View rootView = getWindow().getDecorView().getRootView();

        if (rootView == null) return;

        Snackbar.make(getWindow().getDecorView().getRootView(),
                R.string.loading_snack_cancel, Snackbar.LENGTH_SHORT).show();
    }

    abstract void onLoadFinishedCallback();
}

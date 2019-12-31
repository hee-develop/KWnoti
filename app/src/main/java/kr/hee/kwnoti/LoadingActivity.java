package kr.hee.kwnoti;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import kr.hee.kwnoti.info_activity.OnLoadData;

public abstract class LoadingActivity extends Activity implements OnLoadData {
    protected Dialog loadingDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDialog();

        if (loadingDialog == null) {
            Toast.makeText(this, "Cannot find dialog", Toast.LENGTH_SHORT).show();
        }
        loadingDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.dismiss();
    }

    public abstract void setDialog();

    public void onLoadStart() {
        if (loadingDialog != null) loadingDialog.show();
    }

    @Override public void onLoadFinished() {
        if (loadingDialog != null) loadingDialog.hide();
    }
}

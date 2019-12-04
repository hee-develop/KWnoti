package kr.hee.kwnoti;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import kr.hee.kwnoti.info_activity.OnLoadData;

public abstract class LoadingActivity extends Activity implements OnLoadData {
    protected Dialog loadingDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDialog();
        loadingDialog.show();
    }

    public abstract void setDialog();

    public void onLoadStart() {
        if (loadingDialog != null) loadingDialog.show();
    }

    @Override public void onLoadFinished() {
        if (loadingDialog != null) loadingDialog.hide();
    }
}

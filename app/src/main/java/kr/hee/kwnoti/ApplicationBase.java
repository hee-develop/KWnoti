package kr.hee.kwnoti;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;

import com.tsengvn.typekit.Typekit;

/** 폰트 적용을 위한 어플리케이션 베이스 클래스 */
public class ApplicationBase extends Application {
    private static ApplicationBase applicationBase;

    public static ApplicationBase getInstance() {
        return applicationBase;
    }

    @Override public void onCreate() {
        super.onCreate();
        // Typekit 설정
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "NanumBarunGothicLight.otf"))
                .addBold(Typekit.createFromAsset(this, "NanumBarunGothic.otf"))
                .add("ExtraBold", Typekit.createFromAsset(this, "NanumBarunGothicBold.otf"));
        applicationBase = this;
    }

    private Dialog loadingDialog;

    public void attachCancelListener(DialogInterface.OnCancelListener cancelListener) {
        if (loadingDialog == null) return;

        loadingDialog.setOnCancelListener(cancelListener);
    }

    /** 로딩중 다이얼로그 생성 메소드
     * @param activity    다이얼로그를 생성할 액티비티 */
    public void loadStart(Activity activity) {
        // 액티비티가 없는 경우 표시하지 않음
        if (activity == null || activity.isFinishing()) return;

        // 다이얼로그가 없는 경우 생성
        if (loadingDialog  == null || !loadingDialog.isShowing()) {
            loadingDialog = new Dialog(activity);
            loadingDialog.setCancelable(true);
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setContentView(R.layout.dialog_loading);
            loadingDialog.show();
        }

        // 이미지 애니메이션 동작
        final ImageView img_loading  = (ImageView)loadingDialog.findViewById(R.id.loading_gif);
        final AnimationDrawable anim = (AnimationDrawable)img_loading.getDrawable();
        img_loading.post(new Runnable() {
            @Override public void run() {
                anim.start();
            }
        });
    }

    /** 로딩중 다이얼로그 소멸 메소드 */
    public void loadFinish() {
        if (loadingDialog != null && loadingDialog.isShowing())
            loadingDialog.dismiss();
    }
}

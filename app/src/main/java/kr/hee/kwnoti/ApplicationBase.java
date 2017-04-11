package kr.hee.kwnoti;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

/** 폰트 적용을 위한 어플리케이션 베이스 클래스 */
public class ApplicationBase extends Application {
    @Override public void onCreate() {
        super.onCreate();
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "NanumBarunGothicLight.otf"))
                .addBold(Typekit.createFromAsset(this, "NanumBarunGothic.otf"))
                .add("ExtraBold", Typekit.createFromAsset(this, "NanumBarunGothicBold.otf"));
    }
}

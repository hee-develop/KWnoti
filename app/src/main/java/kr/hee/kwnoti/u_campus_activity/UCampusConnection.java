package kr.hee.kwnoti.u_campus_activity;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/** 유캠퍼스 연결 객체를 만드는 클래스 */
public final class UCampusConnection {
    //싱글톤 객체
    private final static UCampusConnection connection = new UCampusConnection();
    private UCampusConnection() { }
    public static UCampusConnection getInstance() {
        return connection;
    }

    private void UCampusContextInit(Context context) {
        if (context == null) return;

        if (UCampusConnection.context == null)
            UCampusConnection.context = context;
    }

    private void UCampusInit() {
        getUCamLoginRetrofit();     // Retrofit 객체 생성
        getUCamLoginInterface();    // Interface 객체 생성
    }

    private static Context context;
    private static Retrofit retrofit = null;
    private static Interface uCampusInterface = null;

    public Interface getUCamInterface() throws NoContextException {
        if (context == null)
            throw new NoContextException("Context가 null입니다.");

        // 인터페이스가 정의돼있지 않다면 생성
        if (uCampusInterface == null)
            UCampusInit();
        return uCampusInterface;
    }
    public Interface getUCamInterface(Context context) {
        UCampusContextInit(context);
        return getUCamInterface();
    }


    /** 유캠퍼스 연결을 위한 Retrofit 객체 생성 메소드.
     *  로그인 쿠키를 삽입해 OkHttp 객체를 만들어 추가 */
    private void getUCamLoginRetrofit() {
        // 유캠퍼스 로그인을 위한 쿠키(반응과 요청에 대한 인터셉터)
        AddCookieInterceptor cookieAdder = new AddCookieInterceptor(context);
        ReceivedCookieInterceptor cookieInterceptor = new ReceivedCookieInterceptor(context);
        // 인터셉터가 추가된 클라이언트를 반환
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS)
                .addNetworkInterceptor(cookieAdder).addInterceptor(cookieInterceptor).build();
        // Retrofit 빌드
        retrofit = new Retrofit.Builder().client(client).baseUrl(Interface.LOGIN_URL).build();
    }

    /** 유캠퍼스 접속을 위한 Retrofit interface 생성 메소드 */
    private void getUCamLoginInterface() {
        uCampusInterface = retrofit.create(Interface.class);
    }
}
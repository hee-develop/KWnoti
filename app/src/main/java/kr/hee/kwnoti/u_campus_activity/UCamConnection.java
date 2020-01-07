package kr.hee.kwnoti.u_campus_activity;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/** 유캠퍼스 연결 객체를 만드는 클래스 */
public final class UCamConnection {
    //싱글톤 객체
    private final static UCamConnection connection = new UCamConnection();
    private UCamConnection() { }
    public static UCamConnection getInstance() {
        return connection;
    }

    private void UCampusContextInit(Context context) {
        if (context == null) return;

        if (UCamConnection.context == null)
            UCamConnection.context = context;
    }

    private void UCampusInit() {
        getUCamLoginRetrofit();     // Retrofit 객체 생성
        getUCamLoginInterface();    // UCamConnectionInterface 객체 생성
    }

    private static Context context;
    private static Retrofit retrofit = null;
    private static UCamConnectionInterface uCamConnectionInterface = null;

    private UCamConnectionInterface getUCamInterface() {
//        if (context == null)
//            throw new NoContextException("Context가 null입니다.");

        // 인터페이스가 정의돼있지 않다면 생성
        if (uCamConnectionInterface == null)
            UCampusInit();
        return uCamConnectionInterface;
    }
    public UCamConnectionInterface getUCamInterface(Context context) {
        if (UCamConnection.context == null)
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
        retrofit = new Retrofit.Builder().client(client).baseUrl(UCamConnectionInterface.LOGIN_URL).build();
    }

    /** 유캠퍼스 접속을 위한 Retrofit interface 생성 메소드 */
    private void getUCamLoginInterface() {
        uCamConnectionInterface = retrofit.create(UCamConnectionInterface.class);
    }
}

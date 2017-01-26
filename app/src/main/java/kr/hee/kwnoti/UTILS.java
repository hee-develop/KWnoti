package kr.hee.kwnoti;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class UTILS {
    public final static int INTERNET_CONNECTED_ERROR    = -1;
    public final static int INTERNET_DISCONNECTED       = 0;
    public final static int INTERNET_CONNECTED          = 1;
    /** 인터넷 연결 상태를 반환하는 메소드
     * @return 인터넷 연결 상태(위에 있는 상수로 반환) */
    public static int getInternetState(Context context) {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        int internetState;

        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info == null)               internetState = INTERNET_DISCONNECTED;
        else if (info.isConnected())    internetState = INTERNET_CONNECTED;
        else                            internetState = INTERNET_CONNECTED_ERROR;

        return internetState;
    }
}

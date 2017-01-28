package kr.hee.kwnoti;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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

    /** EditText 뷰에서 키보드를 강제로 띄워 주는 메소드
     * @param context     EditText 뷰가 떠 있는 액티비티
     * @param editText    EditText 뷰 */
    public static void appearKeyboard(Context context, EditText editText) {
        editText.requestFocus();
        InputMethodManager manager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }
    /** EditText 뷰에서 키보드를 강제로 내려 주는 메소드
     * @param context     EditText 뷰가 떠 있는 액티비티
     * @param editText    EditText 뷰 */
    public static void disappearKeyboard(Context context, EditText editText) {
        InputMethodManager manager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}

package kr.hee.kwnoti;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public final class UTILS {
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

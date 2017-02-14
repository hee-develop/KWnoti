package kr.hee.kwnoti;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

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

    /** 토스트를 출력하는 메소드
     * 출력 메소드가 너무 길어서 여기서 간편하게 구현
     * @param context    출력할 액티비티
     * @param value      출력할 값(resId 입력 받는 경우엔 string 아이디) */
    public static void showToast(Context context, String value) {
        Toast.makeText(context, value, Toast.LENGTH_SHORT).show();
    }
    public static void showToast(Context context, int resId) {
        showToast(context, context.getString(resId));
    }
}

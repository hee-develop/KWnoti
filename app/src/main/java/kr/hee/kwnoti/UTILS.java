package kr.hee.kwnoti;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

/** 프로젝트에서 자주 쓰이는 기능들을 모아 둔 클래스 */
public final class UTILS {
    // ============================= 화면에 키보드를 띄우거나 / 내리는 메소드 =============================
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

    // ============================== 토스트 출력을 짧게 만들어주는 메소드 ===============================
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

    // ========================== 리스트 선택형 다이얼로그를 짧게 만들어주는 메소드 =========================
    /** 리스트 다이얼로그를 출력하는 메소드
     * @param context          다이얼로그를 출력할 액티비티
     * @param title            다이얼로그 제목
     * @param list             다이얼로그 내용
     * @param clickListener    클릭 시 할 행동 */
    public static void showAlertDialog(Context context, String title, String[] list,
                                       DialogInterface.OnClickListener clickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final String text = "<b><font color=#795548>" + title + "</font></b>";
        final CharSequence htmlTitle;
        // fromHtml(char[]) 메소드가 Nougat 이상에서 제외되었음. 24는 누가의 API 버전
        if (Build.VERSION.SDK_INT >= 24) htmlTitle = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        else                             htmlTitle = Html.fromHtml(text);
        // 다이얼로그 모양 생성 및 출력
        builder.setTitle(htmlTitle);
        builder.setItems(list, clickListener);
        builder.show();
    }

    // ========================== 유캠퍼스 로그인 인터페이스를 만들어 주는 메소드 ==========================
    /** 유캠퍼스 로그인을 위해 쿠키 인터셉터를 추가해주는 메소드
     * @param context    로그인을 시도하는 액티비티
     * @param baseUrl    로그인을 시도 할 URL
     * @return           만들어진 Interface 객체(이 객체로 연결을 시도할 수 있음) */
//    public static Interface makeRequestClientForUCampus(Context context, String baseUrl) {
//        // 유캠퍼스 로그인을 위한 쿠키(반응과 요청에 대한 인터셉터)
//        AddCookieInterceptor cookieAdder = new AddCookieInterceptor(context);
//        ReceivedCookieInterceptor cookieInterceptor = new ReceivedCookieInterceptor(context);
//        // 인터셉터가 추가된 클라이언트를 반환
//        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS)
//                .addNetworkInterceptor(cookieAdder).addInterceptor(cookieInterceptor).build();
//        // Retrofit 빌드
//        Retrofit retrofit = new Retrofit.Builder().client(client).baseUrl(baseUrl).build();
//        return retrofit.create(Interface.class);
//    }

    // ============================ 사용자 정보가 들어 있는지 확인해주는 메소드 ===========================
    /** SharedPreferences 내부에 사용자 정보가 있는지 확인하는 메소드
     * @return      사용자 정보가 들어 있는 SharedPreferences, 정보를 못찾았으면 null 반환 */
    public static SharedPreferences checkUserData(Context context) {
        // SharedPreferences 데이터를 불러 옴
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String studentId = pref.getString(context.getString(R.string.key_studentID), "");
        if (studentId.length() < 1) {
            showToast(context, R.string.toast_check_user);
            return null;
        }
        else return pref;
    }


}

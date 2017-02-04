package kr.hee.kwnoti.settings_activity;

import android.content.Context;
import android.preference.EditTextPreference;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import kr.hee.kwnoti.R;

/** 설정의 EditTextPreference 뷰를 대체할 자동 완성형 EditTextPreference
 * 기존의 EditText 뷰를 자동완성으로 교체한 것 외엔 큰 차이 없음 */
public class AutoCompleteEditTextPreference extends EditTextPreference {
    private AutoCompleteTextView autoEditText;

    // 생성자, 세 생성자가 반드시 전부 존재해야 오류가 생기지 않음!
    public AutoCompleteEditTextPreference(Context context) {
        super(context);
    }
    public AutoCompleteEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public AutoCompleteEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        // 기존의 EditText 뷰의 값들을 추출
        EditText editText = (EditText)view.findViewById(android.R.id.edit);
        String currentValue = editText.getText().toString();
        ViewGroup viewGroup = (ViewGroup)editText.getParent();
        ViewGroup.LayoutParams layoutParams = editText.getLayoutParams();
        // EditText 제거
        viewGroup.removeView(editText);

        // AutoCompleteTextView 뷰 생성 및 EditText 값 대입
        autoEditText = new AutoCompleteTextView(getContext());
        autoEditText.setLayoutParams(layoutParams);
        autoEditText.setId(android.R.id.edit);
        autoEditText.setText(currentValue);
        // 인풋값 설정 및 어댑터 추가
        autoEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        autoEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        String[] majors = getContext().getResources().getStringArray(R.array.majors);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_dropdown_item_1line, majors);
        autoEditText.setAdapter(adapter);
        // AutoCompleteTextView 추가
        viewGroup.addView(autoEditText);
    }

    /** Dialog 내 EditText 반환 메소드. 여기서는 EditText 대신 자동완성 뷰를 반환시켜 줌 */
    @Override public EditText getEditText() {
        return autoEditText;
    }

    /** Dialog 종료 리스너
     * @param positiveResult    OK로 닫힌건지 여부 */
    @Override protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        // OK 시 변경된 값 저장
        if (positiveResult)
            setText(autoEditText.getText().toString());
    }
}

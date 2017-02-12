package kr.hee.kwnoti.student_card_activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import kr.hee.kwnoti.R;
import kr.hee.kwnoti.UTILS;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class StudentCardActivity extends Activity {
    private static final String TAG = "StudentCard";
    // 뷰
    TextView    studentId, studentName, studentMajor;
    ImageView   qrCodeView;
    // 학번
    static String   ID = null;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_card);
        setTitle(R.string.studentCard_title);

        // 뷰 초기화
        initView();
    }

    /** 화면이 보여질 때마다 자동으로 인증 & QR 새로고침 */
    @Override protected void onResume() {
        super.onResume();
        certificate(ID);
    }

    /** 뷰 초기화 메소드 */
    void initView() {
        studentId   = (TextView)findViewById(R.id.studentCard_ID);
        studentName = (TextView)findViewById(R.id.studentCard_name);
        studentMajor= (TextView)findViewById(R.id.studentCard_major);
        qrCodeView  = (ImageView)findViewById(R.id.studentCard_qrCode);
        // QR 코드를 누르면 새로고침
        qrCodeView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                certificate(ID);
            }
        });

        // 설정 값을 통해 뷰 데이터 설정 및 ID값 설정
        String loadFailed = getString(R.string.text_loadFailed);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        studentId.setText(pref.getString(getString(R.string.key_studentID), loadFailed));
        studentName.setText(pref.getString(getString(R.string.key_studentName), loadFailed));
        studentMajor.setText(pref.getString(getString(R.string.key_studentMajor), loadFailed));
        ID = "0" + pref.getString(getString(R.string.key_studentID), "");

        Log.d(TAG, "Current student ID : " + ID);
    }

    /** 중앙도서관 서버에서 인증 시도 메소드. QR 코드 생성도 여기서 호출 */
    boolean certificate(String id) {
        // 변수 예외 처리
        if (id == null || id.equals("0")) return false;

        // Retrofit 객체
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PostInterface.URL)
                .addConverterFactory(SimpleXmlConverterFactory.create()).build();
        // Retrofit 객체로 인터넷 연결을 시도 할 어노테이션 인터페이스
        PostInterface postInterface = retrofit.create(PostInterface.class);
        // Base64 인코드 및 연결 설정(encodedId, Y 두 개가 POST 할 메세지)
        final String encodedId = Base64.encodeToString(id.getBytes(), Base64.DEFAULT);
        Call<PostResult> body = postInterface.getBody(encodedId, "Y");
        // Async 연결 시도
        body.enqueue(new Callback<PostResult>() {
            // 인증 성공
            @Override public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                // 정상 인증인 경우 QR 코드 생성 및 사용 가능
                if (response.body().resultMsg.equals("정상")) {
                    QRCodeGenerator(response.body().qrCode);
                    Log.d(TAG, "QR refreshed : " + response.body().qrCode);
                }
                else UTILS.showToast(StudentCardActivity.this, "인증에 문제가 발생했습니다.");
            }
            // 연결 혹은 인증 실패
            @Override public void onFailure(Call<PostResult> call, Throwable err) {
                UTILS.showToast(StudentCardActivity.this, "인증에 실패했습니다.");
                err.printStackTrace();
            }
        });
        return true;
    }

    /** QRCodeGenerator
     * QR 코드 생성 메소드. QR 이미지 뷰어에 자동으로 값 반환
     * @param qrValue  QR 코드를 생성할 데이터
     * @return boolean 학번 생성 여부 반환 */
    boolean QRCodeGenerator(String qrValue) {
        // 학번이 제대로 전달이 안되면 null 리턴
        if (qrValue.isEmpty()) return false;

        // QR 코드 생성
        MultiFormatWriter generator = new MultiFormatWriter();
        try {
            final int WIDTH = 600, HEIGHT = 600;
            BitMatrix matrix = generator.encode(qrValue, BarcodeFormat.QR_CODE, WIDTH, HEIGHT);
            Bitmap qrCode = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < WIDTH; i++)
                for (int j = 0; j < HEIGHT; j++)
                    qrCode.setPixel(i, j, matrix.get(i, j) ? Color.BLACK : Color.WHITE);

            // 생성된 QR 코드를 적용
            final Bitmap QR_CODE = qrCode;
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    qrCodeView.setImageBitmap(QR_CODE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}

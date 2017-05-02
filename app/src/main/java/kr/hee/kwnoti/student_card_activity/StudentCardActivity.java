package kr.hee.kwnoti.student_card_activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import kr.hee.kwnoti.KEY;
import kr.hee.kwnoti.R;
import kr.hee.kwnoti.UTILS;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/** 학생증 액티비티 */
public class StudentCardActivity extends Activity {
    // 뷰
    TextView    studentId, studentName, studentMajor;
    // 세로 화면의 QR 코드 이미지뷰
    ImageView   qrCodeView;
    // 가로 화면의 학생증 이미지뷰
    ImageView   studentImage;

    // 사용자 정보 및 학번
    SharedPreferences pref;
    static String   ID = null;
    // 밝기에 대한 변수
    WindowManager.LayoutParams params;
    float brightness;
    // 화면 방향
    int deviceOrientation;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.studentCard_title);

        // 사용자 정보 유무 확인
        if ((pref = UTILS.checkUserData(this)) == null) {
            finish();
            return;
        }

        // 뷰 초기화 및 ID 설정
        else ID = "0" + pref.getString(getString(R.string.key_studentID), "");

        // 밝기에 대한 변수 설정
        params = getWindow().getAttributes();

        // 화면 방향 확인 및 화면 구성
        deviceOrientation = getResources().getConfiguration().orientation;
        initView(pref, deviceOrientation);
    }

    /** 화면이 보여질 때마다 자동으로 인증 & QR 새로고침 */
    @Override protected void onResume() {
        super.onResume();
        if (deviceOrientation == Configuration.ORIENTATION_PORTRAIT)
            certificate(ID);

        // 기존 밝기 저장 및 최대밝기로 설정
        brightness = params.screenBrightness;
        params.screenBrightness = 1f;
        getWindow().setAttributes(params);
    }

    /** 화면이 가려질 때마다 밝기를 기존 밝기로 변경 */
    @Override protected void onPause() {
        super.onPause();

        // 기존 밝기로 변경
        params.screenBrightness = brightness;
        getWindow().setAttributes(params);
    }

    /** 화면이 돌아가면 돌아간 화면에 맞게 뷰 재설정 */
    @Override public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        deviceOrientation = getResources().getConfiguration().orientation;
        initView(pref, deviceOrientation);

        // 화면이 돌아가고 세로 화면이 됐을 때 QR 코드 재생성
        if (deviceOrientation == Configuration.ORIENTATION_PORTRAIT)
            certificate(ID);
    }

    /** 뷰 초기화 메소드 */
    void initView(SharedPreferences pref, int orientation) {
        String  loadFailed = getString(R.string.toast_loadFailed);
        String  stuId   = pref.getString(getString(R.string.key_studentID), loadFailed),
                stuName = pref.getString(getString(R.string.key_studentName), loadFailed),
                stuMajor= pref.getString(getString(R.string.key_studentMajor), loadFailed);

        // 세로 화면
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_student_card);

            qrCodeView  = (ImageView)findViewById(R.id.studentCard_qrCode);
            // QR 코드를 누르면 새로고침
            qrCodeView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    certificate(ID);
                }
            });
        }
        // 가로 화면
        else {
            setContentView(R.layout.activity_student_card);

            studentImage = (ImageView)findViewById(R.id.studentCard_portrait);

            Bitmap stuImage = BitmapFactory.decodeFile(getFilesDir() + "/" + KEY.STUDENT_IMAGE);
            if (stuImage != null)
                studentImage.setImageBitmap(stuImage);
        }

        studentId   = (TextView)findViewById(R.id.studentCard_ID);
        studentName = (TextView)findViewById(R.id.studentCard_name);
        studentMajor= (TextView)findViewById(R.id.studentCard_major);

        studentId.setText(stuId);
        studentName.setText(stuName);
        studentMajor.setText(stuMajor);
    }

    /** 중앙도서관 서버에서 인증 시도 메소드. QR 코드 생성도 여기서 호출 */
    boolean certificate(String id) {
        // 변수 예외 처리
        if (id == null || id.equals("0")) return false;

        // Retrofit 객체
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Interface.URL)
                .addConverterFactory(SimpleXmlConverterFactory.create()).build();
        // Retrofit 객체로 인터넷 연결을 시도 할 어노테이션 인터페이스
        Interface postInterface = retrofit.create(Interface.class);
        // Base64 인코드 및 연결 설정(encodedId, Y 두 개가 POST 할 메세지)
        final String encodedId = Base64.encodeToString(id.getBytes(), Base64.DEFAULT);
        Call<Result> body = postInterface.getBody(encodedId, "Y");

        // Async 연결 시도
        body.enqueue(new Callback<Result>() {
            // 인증 성공
            @Override public void onResponse(Call<Result> call, Response<Result> response) {
                // 정상 인증인 경우 QR 코드 생성 및 사용 가능
                if (response.body().resultMsg.equals("정상")) {
                    try {
                        QRCodeGenerator(response.body().qrCode);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    UTILS.showToast(StudentCardActivity.this, getString(R.string.toast_refreshed));
                }
                else UTILS.showToast(StudentCardActivity.this, getString(R.string.toast_certificate_failed));
            }
            // 연결 혹은 인증 실패
            @Override public void onFailure(Call<Result> call, Throwable err) {
                UTILS.showToast(StudentCardActivity.this, getString(R.string.toast_certificate_failed));
                err.printStackTrace();
            }
        });
        return true;
    }

    /** QRCodeGenerator
     * QR 코드 생성 메소드. QR 이미지 뷰어에 자동으로 값 반환
     * @param qrValue  QR 코드를 생성할 데이터
     * @return boolean 학번 생성 여부 반환 */
    boolean QRCodeGenerator(String qrValue) throws WriterException {
        // 학번이 제대로 전달이 안되면 null 리턴
        if (qrValue.isEmpty()) return false;

        // QR 코드 생성
        MultiFormatWriter generator = new MultiFormatWriter();
        final int WIDTH = 240, HEIGHT = 240;
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

        return true;
    }
}

package kr.hee.kwnoti;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class StudentCardActivity extends Activity {
    TextView    studentId,
                studentName,
                studentMajor;
    ImageView   qrCodeView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_card);
        setTitle(getString(R.string.studentCard_title));

        initView();
        //Toast.makeText(this, "가로로 돌리면 모양이 변경됩니다.", Toast.LENGTH_SHORT).show();

        // 학생증 데이터를 불러 와 출력
        SharedPreferences qrData = PreferenceManager.getDefaultSharedPreferences(this);
        setData(qrData);
    }

    /** 뷰 초기화(연결) 메소드 */
    void initView() {
        studentId = (TextView)findViewById(R.id.studentCard_ID);
        studentName = (TextView)findViewById(R.id.studentCard_name);
        studentMajor = (TextView)findViewById(R.id.studentCard_major);
        qrCodeView = (ImageView)findViewById(R.id.studentCard_qrCode);
        qrCodeView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                QRCodeGenerator(studentId.getText().toString());
            }
        });
    }

    /** SharedPreferences 값을 불러 학생증 데이터 출력
     * QR 코드 출력도 겸해서 함
     * @param pref    SharedPreferences */
    void setData(SharedPreferences pref) {
        String loadFailed = getString(R.string.text_loadFailed);
        // 학생증 데이터 설정
        studentId.setText(pref.getString(getString(R.string.key_studentID), loadFailed));
        studentName.setText(pref.getString(getString(R.string.key_studentName), loadFailed));
        studentMajor.setText(pref.getString(getString(R.string.key_studentMajor), loadFailed));

        // QR 코드 불러오기
        new Thread(new Runnable() {
            @Override public void run() {
                QRCodeGenerator(studentId.getText().toString());
            }
        }).start();
    }


    /** QRCodeGenerator
     * QR 코드 생성 메소드. QR 이미지 뷰어에 자동으로 값 반환
     * @param studentID 학번을 받아 옴
     * @return boolean 학번 생성 여부 반환 */
    boolean QRCodeGenerator(String studentID) {
        // 학번이 제대로 전달이 안되면 null 리턴
        if (studentID.isEmpty()) return false;

        // QR 코드를 만들 데이터 생성(학번, 시간 등)
        String qrData = "_KW    0";
        Calendar calendar = new GregorianCalendar();
        int qrYear  = calendar.get(Calendar.YEAR),
            qrMonth = calendar.get(Calendar.MONTH),
            qrDate  = calendar.get(Calendar.DATE),
            qrHour  = calendar.get(Calendar.HOUR_OF_DAY),
            qrMinute= calendar.get(Calendar.MINUTE),
            qrSecond= calendar.get(Calendar.SECOND);
        qrData += studentID;
        qrData += qrYear;
        qrData += (qrMonth < 9) ? "0"+(qrMonth+1) : (qrMonth+1);
        qrData += (qrDate < 9)  ? "0"+qrDate : qrDate;
        qrData += (qrHour < 9)  ? "0"+qrHour : qrHour;
        qrData += (qrMinute < 9)? "0"+qrMinute : qrMinute; // TODO 앱에선 시간이 2분정도 빠른데 괜찮나?
        qrData += (qrSecond < 9)? "0"+qrSecond : qrSecond;

        // QR 코드 생성
        MultiFormatWriter generator = new MultiFormatWriter();
        try {
            final int WIDTH = 600, HEIGHT = 600;
            BitMatrix matrix = generator.encode(qrData, BarcodeFormat.QR_CODE, WIDTH, HEIGHT);
            Bitmap qrCode = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < WIDTH; i++)
                for (int j = 0; j < HEIGHT; j++)
                    qrCode.setPixel(i, j, matrix.get(i, j) ? Color.BLACK : Color.WHITE);

            // 생성된 QR 코드를 적용
            final Bitmap QRCODE = qrCode;
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    qrCodeView.setImageBitmap(QRCODE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}

package kr.hee.kwnoti.settings_activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import kr.hee.kwnoti.KEY;
import kr.hee.kwnoti.R;

/** 설정 액티비티. XML 파일을 불러 화면에 띄워줌 */
public class SettingsActivity extends AppCompatActivity {
    // 인텐트의 requestCode
    private static final int PICK_FROM_GALLERY = 122;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
//        setTitle(R.string.main_settings);
//        getSupportFragmentManager().beginTransaction().replace(
//                R.id.setting_frame, new SettingsFragment()).commit();
    }

    /** 설정 화면을 위한 프래그먼트. settings.xml 데이터를 가져 옴 */
    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {
        EditTextPreference  studentId,
                            studentName,
                            studentMajor;
        Preference          studentImage;
        String key_stuId, key_stuName, key_stuMajor, key_stuImage;

        @Override public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            pref.registerOnSharedPreferenceChangeListener(this);

            // 뷰 초기화
            initView();
            setData(pref);
        }

        /** Summary 값을 써 주기 위한 변수 선언 */
        void initView() {
            key_stuId   = getString(R.string.key_stuID);
            key_stuName = getString(R.string.key_stuName);
            key_stuMajor= getString(R.string.key_stuMajor);
            key_stuImage= getString(R.string.key_stuImage);

            studentId   = (EditTextPreference)findPreference(key_stuId);
            studentName = (EditTextPreference)findPreference(key_stuName);
            studentMajor= (EditTextPreference)findPreference(key_stuMajor);
            studentImage= findPreference(key_stuImage);

            studentImage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override public boolean onPreferenceClick(Preference preference) {
                    if (isCameraPermissionGranted()) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                                .setType("image/*")
                                .addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent, PICK_FROM_GALLERY);
                    }
                    else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setMessage("사진 수정엔 권한이 필요합니다.");
                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[] { Manifest.permission.CAMERA,
                                        Manifest.permission.READ_EXTERNAL_STORAGE }, 99);
                            }
                        });
                        dialog.show();
                    }
                    return false;
                }
            });
        }

        boolean isCameraPermissionGranted() {
            return (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED);
        }

        /** 이미지 크롭을 위한 리스너 */
        @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
            // 설정 액티비티
            Activity sActivity = getActivity();

            if (resultCode != RESULT_OK) return;

            switch (requestCode) {
                // 갤러리에서 이미지를 가져왔을 경우
                case PICK_FROM_GALLERY:
                    // 이미지 추출
                    final Uri uri = data.getData();

                    int     kwRed = getResources().getColor(R.color.kwRed),
                            kwDRed = getResources().getColor(R.color.kwRedDark);

                    UCrop.Options uOptions = new UCrop.Options();
                    uOptions.setToolbarColor(kwRed);
                    uOptions.setStatusBarColor(kwDRed);
                    uOptions.setActiveWidgetColor(kwRed);
                    uOptions.setToolbarTitle("학생증 이미지 설정");

                    // 이미지를 갤러리에서 가져옴
                    String tempFile = "stuImgTmp";
                    UCrop uCrop = UCrop.of(uri,
                            Uri.fromFile(new File(sActivity.getCacheDir(), tempFile)))
                            .withAspectRatio(3.5f, 4.5f)
                            .withOptions(uOptions);
                    uCrop.start(sActivity.getApplicationContext(), this);
                    break;

                // 크롭이 완료된 경우
                case UCrop.REQUEST_CROP:
                    File file = new File(getActivity().getFilesDir(), KEY.STUDENT_IMAGE);
                    Bitmap image;
                    try {
                        image = MediaStore.Images.Media.getBitmap(sActivity.getContentResolver(),
                                UCrop.getOutput(data));
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }

                    // 이미지 리사이즈
                    int vWidth = getResources().getDimensionPixelOffset(R.dimen.stuImage_width),
                        vHeight= getResources().getDimensionPixelOffset(R.dimen.stuImage_height);
                    image = Bitmap.createScaledBitmap(image, vWidth, vHeight, true);

                    // 파일 쓰기
                    try {
                        OutputStream os = new FileOutputStream(file);
                        image.compress(Bitmap.CompressFormat.JPEG, 90, os);
                        os.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        image.recycle();
                    }
                    break;
            }
        }

        /** Summary 값 변경 */
        void setData(SharedPreferences pref) {
            studentId.setSummary(pref.getString(key_stuId, ""));
            studentName.setSummary(pref.getString(key_stuName, ""));
            studentMajor.setSummary(pref.getString(key_stuMajor, ""));
        }

        @Override public void onPause() {
            super.onPause();
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            pref.unregisterOnSharedPreferenceChangeListener(this);
        }

        /** onSharedPreferenceChanged
         * 설정에서 값이 변경됐을 때 호출
         * @param pref  설정 SharedPreference
         * @param key   호출된 key 값 */
        @Override public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
            // 푸쉬 알림 켬/끔
            if (key.equals(getString(R.string.key_pushActive))) {
                final FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
                if (pref.getBoolean(key, true)) firebaseMessaging.subscribeToTopic("notice");
                else                            firebaseMessaging.unsubscribeFromTopic("notice");
            }
            else setData(pref);

            // 학번이나 비밀번호가 변경된 경우, 쿠키를 초기화 함
            if (key.equals(getString(R.string.key_stuID))
                    || key.equals(getString(R.string.key_stuUCampusPwd))) {
                SharedPreferences.Editor userPrefEdit = PreferenceManager.
                        getDefaultSharedPreferences(getActivity()).edit();
                userPrefEdit.putStringSet(KEY.COOKIE_SET, null);
                userPrefEdit.apply();
            }
        }
    }
}
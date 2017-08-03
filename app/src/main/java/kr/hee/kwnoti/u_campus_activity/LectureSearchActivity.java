package kr.hee.kwnoti.u_campus_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kr.hee.kwnoti.ActivityLoadingBase;
import kr.hee.kwnoti.BrowserActivity;
import kr.hee.kwnoti.KEY;
import kr.hee.kwnoti.R;
import kr.hee.kwnoti.UTILS;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** 강의계획서 검색 액티비티 */
public class LectureSearchActivity extends ActivityLoadingBase {
    // 뷰
    EditText     lectureTitle, professorName;
    RecyclerView recyclerView;
    Button       button;

    // 유캠퍼스 접속을 위한 인터페이스와 콜 객체
    UCamConnectionInterface uCamInterface;
    Call<ResponseBody> call;


    // 스피너와 관련된 변수들
    Spinner[]    spinners = new Spinner[4]; // 스피너

    // 배열 상수
    private static final int YEAR       = 0;
    private static final int SEMESTER   = 1;
    private static final int COMMON     = 2;
    private static final int MAJOR      = 3;

    // 스피너에 삽입할 어댑터와 리스트
    ArrayList<SpinnerData>[] adapterDatas = new ArrayList[4];
    LectureAdapter[] adapters = new LectureAdapter[4];

    /** 스피너의 어댑터 클래스
     * 기존 {@link ArrayAdapter}에서 {@link SpinnerData}의 값을 반환하도록 변경 */
    private class LectureAdapter extends ArrayAdapter {
        LectureAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
            super(context, resource, objects);
        }

        @Nullable @Override public Object getItem(int position) {
            return ((SpinnerData)super.getItem(position)).text;
        }
    }

    /** 스피너의 데이터 클래스
     * 표시할 값과 실제 전송할 값을 저장 */
    private class SpinnerData {
        String value; // 실제 값
        String text;  // 스피너에 출력되는 값

        SpinnerData(String value, String text) {
            this.value = value;
            this.text = text;
        }
    }


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        // 스피너 데이터를 위한 사전 작업
        uCamInterface = UCamConnection.getInstance().getUCamInterface();
        call = uCamInterface.getLectureListMain();
        call.enqueue(new Callback<ResponseBody>() {
            @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> res) {
                String html = getBodyFromResponse(res);

                // 예외처리
                if (html == null) {
                    onFailure(call, new IOException("No body"));
                    return;
                }

                // 스피너에 값을 넣기 위해 데이터 추출 및 삽입
                Document doc = Jsoup.parse(html);
                Elements years = doc.select("select[name=this_year] > option");
                addToArray(years, YEAR);

                Elements semesters = doc.select("select[name=hakgi] > option");
                addToArray(semesters, SEMESTER);

                Elements fsel1 = doc.select("select[name=fsel1] > option");
                addToArray(fsel1, COMMON);

                Elements fsel2 = doc.select("select[name=fsel2] > option");
                addToArray(fsel2, MAJOR);
            }

            @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
                UTILS.showToast(LectureSearchActivity.this, R.string.toast_loadFailed);
            }
        });
    }

    /** 뷰 초기화 메소드 */
    void initView() {
        setContentView(R.layout.activity_lecture_search);

        lectureTitle = (EditText)findViewById(R.id.lecture_edit_lectureTitle);
        professorName= (EditText)findViewById(R.id.lecture_edit_professor);
        spinners[0]  = (Spinner)findViewById(R.id.lecture_spinner_year);
        spinners[1]  = (Spinner)findViewById(R.id.lecture_spinner_semester);
        spinners[2]  = (Spinner)findViewById(R.id.lecture_spinner_common);
        spinners[3]  = (Spinner)findViewById(R.id.lecture_spinner_major);
        recyclerView = (RecyclerView)findViewById(R.id.lecture_recyclerView);
        button       = (Button)findViewById(R.id.lecture_btn_search);

        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                loadStart();

                String year     = (adapterDatas[YEAR].get(spinners[YEAR].getSelectedItemPosition())).value;
                String semester = (adapterDatas[SEMESTER].get(spinners[SEMESTER].getSelectedItemPosition())).value;
                String common   = (adapterDatas[COMMON].get(spinners[COMMON].getSelectedItemPosition())).value;
                String major    = (adapterDatas[MAJOR].get(spinners[MAJOR].getSelectedItemPosition())).value;

                call = uCamInterface.getLectureList(
                        lectureTitle.getText().toString(),
                        professorName.getText().toString(),
                        common, year, semester, major, "00_00");
                call.enqueue(new Callback<ResponseBody>() {
                    @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> res) {
                        String html = getBodyFromResponse(res);

                        Document doc = Jsoup.parse(html);
                        Elements lectures = doc.select("table a[href^=h_lecture]");

                        for (Element element : lectures) {

                        }

                        Intent intent = new Intent(LectureSearchActivity.this, BrowserActivity.class);
                        intent.putExtra(KEY.BROWSER_FROM_LECTURE_NOTE, true);
                        intent.putExtra(KEY.BROWSER_INCLUDE_URL, true);
//                        intent.putExtra(KEY.BROWSER_URL, url);

                        loadFinish();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        });

        // 4개의 스피너를 위한 어댑터 설정 및 삽입
        for (int i = 0; i < 4; i++) {
            adapterDatas[i] = new ArrayList<>();

            adapters[i] = new LectureAdapter(LectureSearchActivity.this,
                    android.R.layout.simple_spinner_item, adapterDatas[i]);
            adapters[i].setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinners[i].setAdapter(adapters[i]);
        }
    }

    /** 강의계획서 검색 데이터를 스피너의 ArrayList 변수에 삽입하는 메소드
     * @param elements    삽입할 데이터
     * @param index       ArrayList 배열에서의 index */
    void addToArray(Elements elements, final int index) {
        for (Element element : elements) {
            String value = element.attr("value");
            String text = element.text();
            if (element.hasAttr("selected"))
                spinners[index].setSelection(element.elementSiblingIndex());
            adapterDatas[index].add(new SpinnerData(value, text));
        }

        runOnUiThread(new Runnable() {
            @Override public void run() {
                adapters[index].notifyDataSetChanged();
            }
        });
    }

    /** 유캠퍼스 인코드 변환 된 HTML 객체를 반환하는 메소드
     * @param res    Retrofit 응답 객체
     * @return       EUC-KR 인코딩으로 변환 된 {@link String}객체 */
    String getBodyFromResponse(Response<ResponseBody> res) {
        String html = null;
        try {
            // EUC-KR 인코딩 변환
            html = new String(res.body().bytes(), "EUC-KR");
        } catch (IOException e) {
            UTILS.showToast(LectureSearchActivity.this, R.string.toast_failed);
//            e.printStackTrace();
        }

        return html;
    }

    @Override public void loadCanceled() {
        // TODO
    }



}

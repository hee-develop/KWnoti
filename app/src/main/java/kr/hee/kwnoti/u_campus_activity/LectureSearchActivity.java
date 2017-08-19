package kr.hee.kwnoti.u_campus_activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import kr.hee.kwnoti.ActivityLoadingBase;
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

    // 강의계획서 리스트를 위한 변수
    LectureListAdapter lectureAdapter;

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
        uCamInterface = UCamConnection.getInstance().getUCamInterface(LectureSearchActivity.this);
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
                addToSpinnerArray(years, YEAR);

                Elements semesters = doc.select("select[name=hakgi] > option");
                addToSpinnerArray(semesters, SEMESTER);

                Elements fsel1 = doc.select("select[name=fsel1] > option");
                addToSpinnerArray(fsel1, COMMON);

                Elements fsel2 = doc.select("select[name=fsel2] > option");
                addToSpinnerArray(fsel2, MAJOR);
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

        recyclerView.setLayoutManager(new LinearLayoutManager(LectureSearchActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(lectureAdapter = new LectureListAdapter(LectureSearchActivity.this));

        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                new LectureSearchThread().start();
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
    void addToSpinnerArray(Elements elements, final int index) {
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


    private class LectureSearchThread extends Thread {
        @Override public void run() {
            super.run();

            // 다이얼로그 생성
            loadStart();

            // 어댑터 내 데이터 삭제
            lectureAdapter.cleanData();

            // 스피너로부터 값을 가져옴
            String year     = (adapterDatas[YEAR].get(spinners[YEAR].getSelectedItemPosition())).value;
            String semester = (adapterDatas[SEMESTER].get(spinners[SEMESTER].getSelectedItemPosition())).value;
            String common   = (adapterDatas[COMMON].get(spinners[COMMON].getSelectedItemPosition())).value;
            String major    = (adapterDatas[MAJOR].get(spinners[MAJOR].getSelectedItemPosition())).value;

            // 강의계획서 검색 객체 생성
            try {
                call = uCamInterface.getLectureList(
                        URLEncoder.encode(lectureTitle.getText().toString(), "EUC-KR"), // 강의명
                        URLEncoder.encode(professorName.getText().toString(), "EUC-KR"),// 교수명
                        URLEncoder.encode(common, "EUC-KR"),
                        URLEncoder.encode(year, "EUC-KR"),
                        URLEncoder.encode(semester, "EUC-KR"),
                        URLEncoder.encode(major, "EUC-KR"),
                        URLEncoder.encode("00_00", "EUC-KR"));                          // 기타등등
            }
            catch (UnsupportedEncodingException e) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        UTILS.showToast(LectureSearchActivity.this, "");
                    }
                });
            }
            finally {
                if (call == null) return;
            }


            try {
                // 동기로 요청(스레드로 생성하기
                Response<ResponseBody> response = call.execute();

                // HTML 추출
                String html = getBodyFromResponse(response);

                // JSoup 파싱 진행
                Document doc = Jsoup.parse(html);
//                Elements lectures = doc.select("table a[href^=h_lecture]");
                Elements lectures = doc.select("table tr");

                // 강의 당 하나씩 리사이클러 뷰 객체 생성
                for (Element element : lectures) {
                    // 강의계획서와 관련되지 않은 내용은 제외
                    if (!element.hasAttr("onmouseover")) continue;

                    Elements lecture = element.select("td");
                    LectureData data = new LectureData(
                            lecture.get(0).text(), // 학정번호
                            lecture.get(1).text(), // 과목명
                            lecture.get(3).text(), // 교과목 구분
                            lecture.get(4).text(), // 학점/시간
                            lecture.get(5).text(), // 교수
                            lecture.get(1).select("a").attr("href") // URL
                    );

                    lectureAdapter.addData(data);
                }

                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        lectureAdapter.notifyDataSetChanged();
//                        recyclerView.setAdapter(lectureAdapter);
                    }
                });
            }
            catch (IOException e) {
                UTILS.showToast(LectureSearchActivity.this, getString(R.string.toast_loadFailed));
//                e.printStackTrace();
                return;
            }
            finally {
                loadFinish();
            }
        }
    }
}

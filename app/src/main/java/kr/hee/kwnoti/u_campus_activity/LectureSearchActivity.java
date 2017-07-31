package kr.hee.kwnoti.u_campus_activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;

import kr.hee.kwnoti.ActivityLoadingBase;
import kr.hee.kwnoti.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LectureSearchActivity extends ActivityLoadingBase {
    EditText     lectureTitle, professorName;
    Spinner      year, semester, commonSubject, major;
    RecyclerView recyclerView;
    Button       button;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        // 스피너 데이터를 위한 사전 작업
        UCamConnectionInterface uCamInterface = UCamConnection.getInstance().getUCamInterface();
        Call<ResponseBody> call = uCamInterface.getLectureListMain();
        call.enqueue(new Callback<ResponseBody>() {
            @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> res) {
                String html = null;
                try {
                    html = new String(res.body().bytes(), "EUC-KR");
//                    html = URLEncoder.encode(res.body().string(), "EUC-KR");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (html == null) {
                    onFailure(call, new IOException("No body"));
                    return;
                }

                Document doc = Jsoup.parse(html);
                Elements elements = doc.select("select[name=this_year]");
            }

            @Override public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    void initView() {
        setContentView(R.layout.activity_lecture_search);

        lectureTitle = (EditText)findViewById(R.id.lecture_edit_lectureTitle);
        professorName= (EditText)findViewById(R.id.lecture_edit_professor);
        year         = (Spinner)findViewById(R.id.lecture_spinner_year);
        semester     = (Spinner)findViewById(R.id.lecture_spinner_semester);
        commonSubject= (Spinner)findViewById(R.id.lecture_spinner_common);
        major        = (Spinner)findViewById(R.id.lecture_spinner_major);
        recyclerView = (RecyclerView)findViewById(R.id.lecture_recyclerView);
        button       = (Button)findViewById(R.id.lecture_btn_search);
    }

    @Override public void loadCanceled() {

    }
}

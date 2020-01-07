package kr.hee.kwnoti.u_campus_activity;

import androidx.appcompat.app.AppCompatActivity;
import kr.hee.kwnoti.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CampusActivity extends AppCompatActivity implements CampusActivityCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus);

        CampusConnection connection = CampusConnection.getInstance();
        connection.getCampusLoginResponse("2014722028", "5813", new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseHtml = null;

                try {
                    // save body for bug(body method cannot call 2 times)
                    ResponseBody res = response.body();

                    if (res == null)
                        throw new NoContentException();

                    responseHtml = new String(res.bytes(), "EUC-KR");

                    if (!(responseHtml.contains("이미 로그인되어")
                            || responseHtml.contains("location.replace")))
                        throw new LoginFailedException();

                    // call u-campus main
                    loginFinished();
                }
                catch (Exception e) {
                    onFailure(call, e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // todo
                t.printStackTrace();
            }
        });
        // load data by callback
    }


    @Override
    public void loginFinished() {
        // load campus main
        CampusConnection.getInstance().getCampusMain(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody res = response.body();

                    if (res == null) {
                        throw new NoContentException();
                    }

                    // load student data
                    campusLoadFinished(res.string());
                }
                catch (Exception e) {
                    onFailure(call, e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // todo
                t.printStackTrace();
            }
        });
    }

    @Override
    public void campusLoadFinished(String campusHtml) {
        Document doc = Jsoup.parse(campusHtml);

        try {
            if (doc.body().toString().length() < 1) {
                throw new NoContentException();
            }
        }
        catch (NoContentException e) {
            e.printStackTrace(); // todo
        }

        Elements elements = doc.select("table.main_box").last()
                .select("td.list_txt");

        for (Element e : elements) {
            String subName;
            ArrayList<String> subTime = new ArrayList<>();
            ArrayList<String> subUrlData = new ArrayList<>();

            // subject name
            Matcher matcher = Pattern.compile("(\\[[가-힣]+][가-힣a-z0-9\\s]+)").matcher(e.html());
            if (matcher.find()) {
                subName = matcher.group(1);
                continue;
            }

            // subject time
            matcher = Pattern.compile("([월화수목금토일]\\s.?[가-힣,\\w]*\\([가-힣\\w\\s]+\\))").matcher(e.html());
            while (matcher.find()) {
                subTime.add(matcher.group(1));
            }

            // subject url
            matcher = Pattern.compile("\\(?'(\\w+)',?\\)?").matcher(e.html());
            while (matcher.find()) {
                subUrlData.add(matcher.group(1));
            }
        }

//                String[] subjectData = element.html().split("\'");
//                // SELC 인강인 경우 값을 다르게 설정해줘야 함
//                if (subjectData.length == 1) {
//                    data.subjId     = "";
//                    data.subjYear   = "";
//                    data.subjTerm   = "";
//                    data.subjClass  = "";
//                }
//                else {
//                    data.subjId = subjectData[1];
//                    data.subjYear = subjectData[3];
//                    data.subjTerm = subjectData[5];
//                    data.subjClass = subjectData[7];
//                }
//                // 신규 과제 혹은 공지사항 여부 확인
//                if (subjectData.length > 9) {
//                    String newSomething = subjectData[16];
//                    String newAssignment = null;
//                    if (subjectData.length > 17) newAssignment = subjectData[24];
//
//                    // 신규 공지사항
//                    if (newSomething.contains("btn_n.gif")) data.newNotice = true;
//                        // 신규 과제
//                    else if (newSomething.contains("btn_w.gif")) data.newAssignment = true;
//
//                    if (newAssignment != null) data.newAssignment = true;
//                }
//
//                // 데이터 추가
//                adapter.addData(data);
//                // 다음 공지 유무 확인 및 파싱 종료
//                if (element != elements.last()) element = elements.get(++i);
//                else                            break;
//            }
//
//            // 데이터 로드가 모두 끝나면 어댑터에 알리고 다이얼로그를 없앰
//            runOnUiThread(new Runnable() {
//                @Override public void run() {
//                    adapter.notifyDataSetChanged();
//                }
//            });
//        }
    }
}

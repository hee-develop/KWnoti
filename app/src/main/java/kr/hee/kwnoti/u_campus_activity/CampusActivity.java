package kr.hee.kwnoti.u_campus_activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import kr.hee.kwnoti.R;
import kr.hee.kwnoti.databinding.ActivityCampusBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CampusActivity extends AppCompatActivity implements CampusActivityCallback {
    ActivityCampusBinding binding;

    CampusAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_campus);
        binding.setActivity(this);

        adapter = new CampusAdapter();
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.campusRecycler.setAdapter(adapter);
        binding.campusRecycler.setLayoutManager(linearLayoutManager);

        CampusConnection connection = CampusConnection.getInstance();
        connection.getCampusLoginResponse("2014722028", "5813", new Callback<ResponseBody>() {
            /* TODO Refactor using Observer */
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseHtml = null;

                try {
                    // save body for bug(body method cannot call 2 times)
                    ResponseBody res = response.body();

                    if (res == null)
                        throw new NoContentException();

                    responseHtml = new String(res.bytes(), "EUC-KR");

                    // if login failed
                    if (!(responseHtml.contains("이미 로그인되어")
                            || responseHtml.contains("location.replace")))
                        throw new LoginFailedException();

                    // call u-campus main
                    onLoginFinished();
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
    /* TODO have to change to another thread, not using main thread */
    public void onLoginFinished() {
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
                    loadCampusData(res.string());
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
    public void loadCampusData(String campusHtml) {
        Document doc = null;

        try {
            if (campusHtml.length() < 1) {
                throw new NoContentException();
            }

            doc = Jsoup.parse(campusHtml);

            if (doc.body().toString().length() < 1) {
                throw new NoContentException();
            }
        }
        catch (NoContentException e) {
            e.printStackTrace(); // todo
            return;
        }

        Elements elements = doc.select("table.main_box").last().select("tr");

        // list subject
        ArrayList<CampusData> campusDataList = new ArrayList<>();

        for (int i=1; i<elements.size(); i++) {
            String str = elements.get(i).html();

            String subName = "";
            ArrayList<String> subTime = new ArrayList<>();
            ArrayList<String> subUrlData = new ArrayList<>();

            // subject name
            Matcher matcher = Pattern.compile("(\\[[가-힣]+][가-힣a-z0-9\\s]+)").matcher(str);
            if (matcher.find()) {
                subName = matcher.group(1);
            }

            // subject time
            matcher = Pattern.compile("([월화수목금토일]\\s.?[가-힣,\\w]*\\([가-힣\\w\\s]+\\))").matcher(str);
            while (matcher.find()) {
                subTime.add(matcher.group(1));
            }

            // subject url
            matcher = Pattern.compile("\\(?'(\\w+)',?\\)?").matcher(str);
            while (matcher.find()) {
                subUrlData.add(matcher.group(1));
            }

            campusDataList.add(new CampusData(subName, subTime, subUrlData));
        }

        onLoadFinished(campusDataList);
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

    @Override
    public void onLoadFinished(ArrayList<CampusData> arrayList) {
        //////
        CampusAdapter a = (CampusAdapter)binding.campusRecycler.getAdapter();
        if (a == null) {
            // do something
            return;
        }
        a.setCampusDataArr(arrayList);


//        UCampusMainData viewData = array.get(position);
//
//        final String subjName = viewData.subjName;
//        final String[] listType = {
//                KEY.SUBJECT_PLAN, KEY.SUBJECT_INFO, KEY.SUBJECT_UTIL,
//                KEY.SUBJECT_STUDENT, /*TODO 과제제출 비활성화 KEY.SUBJECT_ASSIGNMENT,*/ KEY.SUBJECT_QNA };
//
//        holder.title.setText(subjName);
//        holder.room.setText(viewData.subjPlace);
//        if (viewData.newNotice) holder.newInfo.setVisibility(View.VISIBLE);
//        if (viewData.newAssignment) holder.newAssignment.setVisibility(View.VISIBLE);
//        holder.view.setOnClickListener(new View.OnClickListener() {
//            @Override public void onClick(View view) {
//                UTILS.showAlertDialog(context, subjName, listType,
//                        new DialogInterface.OnClickListener() {
//                            @Override public void onClick(DialogInterface dInterface, int i) {
//                                Intent intent = new Intent(context, UCampusListActivity.class);
//                                intent.putExtra(KEY.SUBJECT, array.get(holder.getAdapterPosition()));
//                                switch (i)
//                                {
//                                    case 0 : intent.putExtra(KEY.SUBJECT_LOAD_TYPE, listType[0]); break;
//                                    case 1 : intent.putExtra(KEY.SUBJECT_LOAD_TYPE, listType[1]); break;
//                                    case 2 : intent.putExtra(KEY.SUBJECT_LOAD_TYPE, listType[2]); break;
//                                    case 3 : intent.putExtra(KEY.SUBJECT_LOAD_TYPE, listType[3]); break;
//                                    case 4 : intent.putExtra(KEY.SUBJECT_LOAD_TYPE, listType[4]); break;
//                                    case 5 : intent.putExtra(KEY.SUBJECT_LOAD_TYPE, listType[5]); break;
//                                    default: intent.putExtra(KEY.SUBJECT_LOAD_TYPE, "Error"); break;
//                                }
//                                // 클릭한 과목에 해당하는 액티비티 생성
//                                context.startActivity(intent);
//                            }
//                        }
//                );
//            }
//        });
    }
}

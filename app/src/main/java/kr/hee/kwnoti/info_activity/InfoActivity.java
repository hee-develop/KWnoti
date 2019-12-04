package kr.hee.kwnoti.info_activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.hee.kwnoti.LoadingActivity;
import kr.hee.kwnoti.R;


public class InfoActivity extends LoadingActivity implements InfoDataReceived {
    // recycler view
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    InfoAdapter adapter;
    ArrayList<InfoData> infoDataArr = new ArrayList<>();

    boolean hasStrongInfo = false;

    ImageView iv_search;
    Spinner sp_category;
    EditText et_search;
    Spinner sp_searchOpt;
    LinearLayout searchBar;

    // url and query
    UrlWithQuery url;
    int currNoticeNumber = 0;


    @Override
    protected void onStart() {
        super.onStart();

        url = new UrlWithQuery("https://www.kw.ac.kr/ko/life/notice.do?mode=list&&articleLimit=10");

        new RequestInfoThread(url.getUrl()).start();
    }

    public class RequestInfoThread extends RequestThread {
        public RequestInfoThread(String baseUrl) {
            super(baseUrl);
        }

        @Override
        public void afterReceived(Document doc) {
            ArrayList<InfoData> infoArray = new ArrayList<>();
            ArrayList<InfoData> StrongInfoArray= new ArrayList<>();

            // if failed
            if (doc == null) {
                return;
            }

            // get information list
            Elements elements = doc.select("div.board-list-box>ul>li");
            for (Element e : elements) {
                Elements title = e.select("a");
                String[] desc = e.select("p").text().split(" \\| ");
                if (desc.length != 3) desc = new String[3];

                InfoData eInfo = new InfoData(
                        title.text().replaceAll("신규게시글|Attachment", ""),
                        title.attr("href"),
                        desc[1]);

                if (e.hasClass("top-notice")) {
                    if (hasStrongInfo) continue;

                    eInfo.isTopTitle = true;
                    StrongInfoArray.add(eInfo);
                }
                else {
                    infoArray.add(eInfo);
                }
            }

            // do not add top information twice
            hasStrongInfo = true;

            // send arrayList
            makeView(StrongInfoArray);
            makeView(infoArray);
        }
    }

    @Override
    public void makeView(ArrayList infoArr) {
        // add data to data array
        for (Object i : infoArr) {
            infoDataArr.add((InfoData)i);
        }

        runOnUiThread(() -> {
            this.onLoadFinished();
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void setDialog() {
        Dialog d = new Dialog(this);
        d.setTitle("ASB");
        loadingDialog = d;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_info);
        recyclerView = findViewById(R.id.info_recyclerView);

        iv_search = findViewById(R.id.info_toolbar_btn);
        searchBar = findViewById(R.id.info_search_layout);
        sp_category = findViewById(R.id.info_toolbar_spinner);
        et_search = findViewById(R.id.info_search_editText);
        sp_searchOpt = findViewById(R.id.info_search_spinner);


        recyclerView.setLayoutManager(layoutManager = new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter = new InfoAdapter(InfoActivity.this, infoDataArr));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                // call next notice
                if (!recyclerView.canScrollVertically(1)) {
                    url.setUrlQuery("article.offset", currNoticeNumber += 10);
                    onLoadStart();
                    new RequestInfoThread(url.getUrl()).start();
                }
                else super.onScrollStateChanged(recyclerView, newState);
            }
        });


        iv_search.setOnClickListener(v -> {
            switch (searchBar.getVisibility()) {
                case View.VISIBLE :
                    searchBar.setVisibility(View.INVISIBLE);
                    break;
                case View.GONE :
                    searchBar.setVisibility(View.VISIBLE);
                    break;
            }
        });

        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String categoryOptionStr = "srCategoryId1";
                switch ((String)sp_category.getItemAtPosition(position)) {
                    default:
                    case "전체" : url.setUrlQuery(categoryOptionStr, ""); break;
                    case "일반" : url.setUrlQuery(categoryOptionStr, "5"); break;
                    case "학사" : url.setUrlQuery(categoryOptionStr, "6"); break;
                    case "학생" : url.setUrlQuery(categoryOptionStr, "16"); break;
                    case "봉사" : url.setUrlQuery(categoryOptionStr, "17"); break;
                    case "등록/장학" : url.setUrlQuery(categoryOptionStr, "18"); break;
                    case "입학" : url.setUrlQuery(categoryOptionStr, "19"); break;
                    case "시설" : url.setUrlQuery(categoryOptionStr, "20"); break;
                    case "병무" : url.setUrlQuery(categoryOptionStr, "21"); break;
                    case "외부" : url.setUrlQuery(categoryOptionStr, "22"); break;
                }

                if (infoDataArr.size() != 0) {
                    infoDataArr.clear();
                    hasStrongInfo = false;
                    onLoadStart();
                    new RequestInfoThread(url.getUrl()).start();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        et_search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currNoticeNumber = 0;
                url.clearUrlQuery("article.offset");

                infoDataArr.clear();

                switch ((String)sp_searchOpt.getSelectedItem()) {
                    case "제목" :
                    case "Title" :
                        url.setUrlQuery("srSearchKey", "article_title"); break;
                    case "내용" :
                    case "Content" :
                        url.setUrlQuery("srSearchKey", "article_text"); break;
                    case "작성자" :
                    case "Writer" :
                        url.setUrlQuery("srSearchKey", "writer_nm"); break;
                    case "제목 + 내용" :
                    case "Title+content" :
                    default :
                        break;
                }
                url.setUrlQuery("srSearchVal", v.getText().toString());
                hasStrongInfo = false;

                onLoadStart();
                new RequestInfoThread(url.getUrl()).start();
            }
            return false;
        });
    }
}
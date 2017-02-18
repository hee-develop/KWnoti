package kr.hee.kwnoti.tel_activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kr.hee.kwnoti.R;

/** 전화번호부를 출력해 주는 어댑터
 * DB는 {@link TelDB} 클래스에서 관리함
 * DB의 그룹에 따라 그룹을 나눠서 표시해줄 수 있음 */
public class TelAdapter extends RecyclerView.Adapter {
    private ArrayList<ListItem> arrayList;
    private TelDB db;
    private LayoutInflater inflater;

    TelAdapter(Context context) {
        db = new TelDB(context);
        arrayList = new ArrayList<>();
        inflater = LayoutInflater.from(context);

        ArrayList<TelData> arr = db.getTelNumber();
        String currGroupName = "";
        for (int i = 0; i < arr.size(); i++) {
            TelData data = arr.get(i);
            if (!data.groupName.equals(currGroupName)) {
                // 그룹 명을 바꿔주고
                currGroupName = data.groupName;
                // 그룹에 대한 분류 데이터 삽입
                arrayList.add(new Header(data.groupName));
            }
            arrayList.add(new Content(data.departName, data.telNumber));
        }
    }

    /** 뷰 홀더를 inflate 시켜주는 메소드
     * @param parent      RecyclerView
     * @return            실체화된 ViewHolder 리턴 */
    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ListItem.GROUP_NAME) {
            View view = inflater.inflate(R.layout.something, parent, false);
            return new HeaderViewHolder(view);
        }
        else {
            View view = inflater.inflate(R.layout.somehting2, parent, false);
            return new ContentViewHolder(view);
        }
    }

    /** 뷰의 타입을 반환하는 메소드
     * @param position    뷰의 위치
     * @return            뷰의 타입(헤더인지 내용인지) */
    @Override public int getItemViewType(int position) {
        return arrayList.get(position).getType();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    interface ListItem {
        int GROUP_NAME = 0;
        int CONTENT = 1;
        int getType();
    }
    class Header implements ListItem {
        HeaderItem item;
        public Header(String head) {
            item = new HeaderItem();
            item.groupName = head;
        }

        @Override public int getType() {
            return GROUP_NAME;
        }
    }
    class Content implements ListItem {
        ContentItem item;
        public Content(String departName, String telNumber) {
            item = new ContentItem();
            item.departName = departName;
            item.telNumber = telNumber;
        }
        @Override public int getType() {
            return CONTENT;
        }
    }
    class HeaderItem {
        String groupName;
    }
    class ContentItem {
        String departName;
        String telNumber;
    }
}

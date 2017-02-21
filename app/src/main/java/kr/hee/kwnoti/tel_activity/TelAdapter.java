package kr.hee.kwnoti.tel_activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kr.hee.kwnoti.R;

/** 전화번호부를 출력해 주는 어댑터
 * DB는 {@link TelDB} 클래스에서 관리함
 * DB의 그룹에 따라 그룹을 나눠서 표시해줄 수 있음 */
class TelAdapter extends RecyclerView.Adapter {
    private ArrayList<ListItem> arrayList;
    private TelDB db;
    private LayoutInflater inflater;
    private Context context;

    TelAdapter(Context context) {
        this.context = context;
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
                // 그룹 삽입
                if (arrayList.size() % 2 == 1) arrayList.add(new Header(""));
                arrayList.add(new Header(data.groupName));
                arrayList.add(new Header("")); // 더미를 끼워서 크기를 맞춤
            }
            // 그룹에 해당되는 데이터 삽입
            arrayList.add(new Content(data.departName, data.telNumber));
        }
    }

    void findData(String data) {
        if (data.equals("")) return;

        arrayList.clear();
        ArrayList<TelData> arr = db.getTelNumber(data);
        String currGroupName = "";
        for (int i = 0; i < arr.size(); i++) {
            TelData telData = arr.get(i);
            if (!telData.groupName.equals(currGroupName)) {
                // 그룹 명을 바꿔주고
                currGroupName = telData.groupName;
                // 그룹 삽입
                if (arrayList.size() % 2 == 1) arrayList.add(new Header(""));
                arrayList.add(new Header(telData.groupName));
                arrayList.add(new Header("")); // 더미를 끼워서 크기를 맞춤
            }
            // 그룹에 해당되는 데이터 삽입
            arrayList.add(new Content(telData.departName, telData.telNumber));
        }

        this.notifyDataSetChanged();
    }

    void cleanData() {
        db.cleanTel();
    }

    /** 뷰 홀더를 inflate 시켜주는 메소드
     * @param parent      RecyclerView
     * @return            실체화된 ViewHolder 리턴 */
    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 그룹명에 대한 XML 인플레이트
        if (viewType == ListItem.GROUP_NAME) {
            View view = inflater.inflate(R.layout.recycler_group_header, parent, false);
            return new GroupViewHolder(view);
        }
        // 그룹 내 데이터에 대한 XML 인플레이트
        else {
            View view = inflater.inflate(R.layout.recycler_content, parent, false);
            return new ContentViewHolder(view);
        }
    }

    /** 뷰의 타입을 반환하는 메소드
     * @param position    뷰의 위치
     * @return            뷰의 타입(헤더인지 내용인지)*/
    @Override public int getItemViewType(int position) {
        return arrayList.get(position).getType();
    }

    /** 뷰 홀더에 데이터를 넣어 주는 메소드
     * 뷰의 타입에 따라 그룹 이름 혹은 전화 데이터를 넣어 줌
     * @param holder      뷰 홀더
     * @param position    리사이클러 뷰에서 표시 될 위치 */
    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == ListItem.GROUP_NAME) {
            Header headerItem = (Header)arrayList.get(position);
            ((GroupViewHolder)holder).group.setText(headerItem.item.groupName);
        }
        else {
            Content contentItem = (Content)arrayList.get(position);
            final String telNumber = contentItem.item.telNumber;
            ((ContentViewHolder)holder).departName.setText(contentItem.item.departName);
            ((ContentViewHolder)holder).telNumber.setText(telNumber);
            ((ContentViewHolder)holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+telNumber)));
                }
            });
        }
    }

    @Override public int getItemCount() { return arrayList.size(); }

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

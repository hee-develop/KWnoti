package kr.hee.kwnoti.u_campus_activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kr.hee.kwnoti.BrowserActivity;
import kr.hee.kwnoti.KEY;
import kr.hee.kwnoti.R;

/** 유캠퍼스 메인화면의 RecyclerView Adapter */
class UCampusListAdapter extends RecyclerView.Adapter<UCampusListViewHolder> {
    private ArrayList<UCampusListData> array;
    private Context context;

    UCampusListAdapter(Context context) {
        array = new ArrayList<>();
        this.context = context;
    }

    /** 뷰 홀더를 inflate 시켜주는 메소드
     * @param parent      RecyclerView
     * @return            실체화된 UCampusMainViewHolder 반환 */
    @Override public UCampusListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_info, parent, false); // 공지사항 레이아웃을 씀!
        return new UCampusListViewHolder(view);
    }

    @Override public void onBindViewHolder(final UCampusListViewHolder holder, final int position) {
        UCampusListData listData = array.get(position);
        final String subjName = listData.title;

        holder.title.setText(subjName);
        holder.whoWrite.setText(listData.writer);
        holder.date.setText(listData.date);
        holder.views.setText(listData.views);
        if (!listData.attachment)
            holder.attachment.setVisibility(View.GONE);
        holder.newInfo.setVisibility(View.GONE);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(context, BrowserActivity.class);
                int position = holder.getAdapterPosition();
                intent.putExtra(KEY.BROWSER_FROM_UCAMPUS, true);
                intent.putExtra(KEY.BROWSER_TITLE, array.get(position).title);
                intent.putExtra(KEY.BROWSER_DATA, array.get(position).id);
                intent.putExtra(KEY.BROWSER_URL, array.get(position).url);
                context.startActivity(intent);
            }
        });
    }

    @Override public int getItemCount() {
        return array.size();
    }

    /** 다음장 데이터 추가를 위한 메소드.
     * 여기서 notifyDataSetChanged 메소드는 부르지 않음 (성능 향상을 위해)
     * @param data    데이터
     * @return        제대로 추가가 되었는지 여부 */
    boolean addData(UCampusListData data) {
        if (data == null) return false;
        array.add(data);
        return true;
    }
}

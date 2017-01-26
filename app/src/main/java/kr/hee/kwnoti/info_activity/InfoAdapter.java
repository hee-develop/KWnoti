package kr.hee.kwnoti.info_activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kr.hee.kwnoti.R;

/** 공지사항을 출력해 주는 어댑터 */
class InfoAdapter extends RecyclerView.Adapter<InfoViewHolder> {
    private ArrayList<InfoData> array;
    private Context context;

    /** InfoAdapter 생성자
     * @param context   액티비티의 context */
    InfoAdapter(Context context) {
        array = new ArrayList<>();
        this.context = context;
    }

    /** 뷰 홀더를 inflate 시켜주는 메소드
     * @param parent      RecyclerView
     * @return            실체화된 InfoViewHolder 반환 */
    @Override public InfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_info, parent, false);
        return new InfoViewHolder(view);
    }

    /** 생성된 ViewHolder 객체에 데이터를 불어 넣는 메소드
     * @param holder      'onCreateViewHolder'에서 만들어진 {@link InfoViewHolder}
     * @param position    'RecyclerView'에서 뷰의 위치(& ArrayList 에서의 위치) */
    @Override public void onBindViewHolder(final InfoViewHolder holder, int position) {
        holder.title.setText(array.get(position).title);
        holder.whoWrite.setText(array.get(position).whoWrite);
        holder.date.setText(array.get(position).date);
        holder.views.setText(array.get(position).views);
        if (array.get(position).attachment) holder.attachment.setVisibility(View.VISIBLE);
        else holder.attachment.setVisibility(View.INVISIBLE);
        if (array.get(position).newInfo)    holder.newInfo.setVisibility(View.VISIBLE);
        else holder.newInfo.setVisibility(View.INVISIBLE);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(array.get(holder.getAdapterPosition()).link));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                // TODO 자체 브라우저 기능 활용
            }
        });
    }

    @Override public int getItemCount() {
        return array.size();
    }

    /** 다음장 데이터 추가를 위한 메소드.
     * 여기서 notifyDataSetChanged 메소드는 부르지 않음 (성능 향상을 위해)
     * @param data    다음장의 데이터
     * @return        제대로 추가가 되었는지 여부 */
    boolean addInfo(InfoData data) {
        if (data == null) return false;
        array.add(data);
        return true;
    }
}

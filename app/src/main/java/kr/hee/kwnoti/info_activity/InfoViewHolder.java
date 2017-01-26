package kr.hee.kwnoti.info_activity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import kr.hee.kwnoti.R;

/** InfoViewHolder
 * RecyclerView 에서 표현 될 각 View, 일종의 Inflater */
class InfoViewHolder extends RecyclerView.ViewHolder {
    View view; // 게시글 하나 전부를 가리킴 (클릭 리스너를 위해 선언)
    TextView title, whoWrite, date, views;
    ImageView newInfo, attachment;

    // 생성자에서 뷰를 연결해줌
    InfoViewHolder(View view) {
        super(view);
        this.view = view.findViewById(R.id.card_info);
        title   = (TextView)view.findViewById(R.id.card_info_title);
        whoWrite= (TextView)view.findViewById(R.id.card_info_writer);
        date    = (TextView)view.findViewById(R.id.card_info_date);
        views   = (TextView)view.findViewById(R.id.card_info_views);
        newInfo = (ImageView)view.findViewById(R.id.card_info_new);
        attachment = (ImageView)view.findViewById(R.id.card_info_attach);
    }
}
package kr.hee.kwnoti.tel_activity;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import kr.hee.kwnoti.R;

/** 전화번호부 내용 뷰 홀더 */
class ContentViewHolder extends RecyclerView.ViewHolder {
    CardView cardView;
    TextView departName,
             telNumber;

    ContentViewHolder(View view) {
        super(view);
        cardView = (CardView)view.findViewById(R.id.content_layout);
        departName = (TextView)view.findViewById(R.id.content_departName);
        telNumber = (TextView)view.findViewById(R.id.content_telNumber);
    }
}

package kr.hee.kwnoti.settings_activity;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import kr.hee.kwnoti.R;

/** XML 뷰를 실체화 시키는 뷰 홀더 */
class PushFilterViewHolder extends RecyclerView.ViewHolder {
    CardView card;
    TextView card_name;

    PushFilterViewHolder(View view) {
        super(view);
        card        = (CardView)view.findViewById(R.id.card_filter);
        card_name   = (TextView)view.findViewById(R.id.card_filter_name);
    }
}

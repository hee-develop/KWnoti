package kr.hee.kwnoti.calendar_activity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import kr.hee.kwnoti.R;

/** XML 뷰를 실체화 시키는 뷰 홀더 */
class CalendarViewHolder extends RecyclerView.ViewHolder {
    TextView    month,
                date,
                content;

    CalendarViewHolder(View view) {
        super(view);
        month       = (TextView)view.findViewById(R.id.card_calendar_month);
        date        = (TextView)view.findViewById(R.id.card_calendar_date);
        content     = (TextView)view.findViewById(R.id.card_calendar_content);
    }
}

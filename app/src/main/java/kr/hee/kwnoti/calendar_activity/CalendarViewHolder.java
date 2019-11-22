package kr.hee.kwnoti.calendar_activity;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import kr.hee.kwnoti.R;

/** XML 뷰를 실체화 시키는 뷰 홀더 */
class CalendarViewHolder extends RecyclerView.ViewHolder {
    TextView    month, date, content; // 학사 일정의 월, 일, 내용

    CalendarViewHolder(View view) {
        super(view);
        month       = (TextView)view.findViewById(R.id.card_calendar_month);
        date        = (TextView)view.findViewById(R.id.card_calendar_date);
        content     = (TextView)view.findViewById(R.id.card_calendar_content);
    }
}

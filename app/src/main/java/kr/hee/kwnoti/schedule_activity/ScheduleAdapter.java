package kr.hee.kwnoti.schedule_activity;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import kr.hee.kwnoti.R;

/**
 * Recycler adapter for schedule activity
 */
public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {
    private Context context;
    private ArrayList<ScheduleData> scheduleArr;
    private SixRowCalendar sixRowCalendar;

    public ScheduleAdapter(Context context) {
        scheduleArr = new ArrayList<>();
        sixRowCalendar = new SixRowCalendar(this::notifyDataSetChanged);
        this.context = context;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ScheduleViewHolder(LayoutInflater.from(
                parent.getContext()).inflate(R.layout.card_schedule, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        // set sunday color
        if (position % 7 == 0) {
            holder.tv_date.setTextColor(Color.RED);
        }
        else {
            holder.tv_date.setTextColor(ContextCompat.getColor(context, R.color.kwDarkGray));
        }

        // set previous/next month's color
        if (position < sixRowCalendar.dayOfPrevMonthLast ||
            position > sixRowCalendar.dayOfNextMonthFirst+ sixRowCalendar.maxDateOfCurrent-1) {
            holder.layout_card.setAlpha(0.2f);
        }
        else {
            holder.layout_card.setAlpha(1f);
        }

        // set data
        holder.tv_date.setText(String.format(Locale.getDefault(),
                "%d", sixRowCalendar.getDate(position)));
    }

    @Override
    public int getItemCount() {
        return sixRowCalendar.getCalendarLength();
    }


    /**
     * ViewHolder for show schedule data
     */
    class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tv_date;
        View     layout_card,
                 mark;

        ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv_date = itemView.findViewById(R.id.card_schedule_date);
            this.layout_card = itemView.findViewById(R.id.card_schedule_layout);
            this.mark = itemView.findViewById(R.id.card_schedule_mark);
        }
    }
}

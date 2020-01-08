package kr.hee.kwnoti.schedule_activity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    private ScheduleDatabase database;
    private Cursor databaseCursor;

    private int displayWidth, displayHeight;

    public ScheduleAdapter(Context context) {
        scheduleArr = new ArrayList<>();
        sixRowCalendar = new SixRowCalendar(this::notifyDataSetChanged);

        database = new ScheduleDatabase(context, null, 1);
        databaseCursor = database.getData(sixRowCalendar.currYear, sixRowCalendar.currMonth);
        databaseCursor.moveToFirst();

        this.context = context;

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        displayWidth = dm.widthPixels;
        displayHeight= dm.heightPixels;
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
            position > sixRowCalendar.dayOfPrevMonthLast+sixRowCalendar.maxDateOfCurrent-1) {
            holder.layout_card.setAlpha(0.2f);
        }
        else {
            holder.layout_card.setAlpha(1f);
        }

        // set view width and height
        ViewGroup.LayoutParams params = holder.layout_card.getLayoutParams();
        params.width = displayWidth / 7;
        params.height = displayWidth / 7;
        holder.layout_card.setLayoutParams(params);

        // set data
        SixRowCalendar.Date currentDate = sixRowCalendar.getDate(position);

        holder.tv_date.setText(String.format(Locale.getDefault(),
                "%d", currentDate.date));

        if (databaseCursor.isAfterLast())
            databaseCursor.moveToFirst();
        while (!databaseCursor.isAfterLast()) {

            int sYear = databaseCursor.getInt(0);
            int sMonth= databaseCursor.getInt(1);
            int sDate = databaseCursor.getInt(2);
            int eYear = databaseCursor.getInt(3);
            int eMonth= databaseCursor.getInt(4);
            int eDate = databaseCursor.getInt(5);
            String content = databaseCursor.getString(6);

            // check if this day has event
            Calendar startCal = Calendar.getInstance();
            startCal.set(sYear, sMonth-1, sDate, 0, 0, 0);
            Calendar endCal = Calendar.getInstance();
            endCal.set(eYear, eMonth-1, eDate, 0, 0, 0);

            Calendar curCal = Calendar.getInstance();
            curCal.set(currentDate.year, currentDate.month-1, currentDate.date, 0, 0, 0);

            if (curCal.getTimeInMillis() >= startCal.getTimeInMillis() &&
                curCal.getTimeInMillis() <= endCal.getTimeInMillis()) {
                holder.layout_card.setBackgroundColor(Color.BLUE);
                break;
            }

            databaseCursor.moveToNext();
        }
    }

    @Override
    public int getItemCount() {
        return sixRowCalendar.getCalendarLength();
    }

    public void a() {
        notifyDataSetChanged();
    }


    /**
     * ViewHolder for show schedule data
     */
    class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tv_date;
        View     layout_card;

        ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv_date = itemView.findViewById(R.id.card_schedule_date);
            this.layout_card = itemView.findViewById(R.id.card_schedule_layout);
        }
    }
}

package kr.hee.kwnoti.info_activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import kr.hee.kwnoti.BrowserActivity;
import kr.hee.kwnoti.R;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

/** Adapter for information activity */
class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoViewHolder> {
    private ArrayList<InfoData> dataArray;
    private Context context;

    InfoAdapter(Context context, ArrayList<InfoData> arr) {
        this.context = context;
        this.dataArray = arr; // soft copy
    }

    @NonNull
    @Override
    public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InfoViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.card_info, parent, false));
    }

    // set data from here
    @Override
    public void onBindViewHolder(@NonNull InfoViewHolder holder, int position) {
        InfoData infoData = dataArray.get(position);
        if (infoData == null) return;

        holder.tv_title.setText(infoData.title);
        holder.tv_date.setText(infoData.date);
        if (infoData.isTopTitle)
            holder.view.setBackgroundColor(Color.RED);
        else
            holder.view.setBackgroundColor(Color.WHITE);

        holder.view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case ACTION_DOWN :
                    holder.tv_title.setMaxLines(2);
                    break;
                case ACTION_UP :
                    Intent intent = new Intent(context, BrowserActivity.class);
                    intent.putExtra("KEY@Info", dataArray.get(holder.getAdapterPosition()).url);
                    context.startActivity(intent);
// TODO                        intent.putExtra(KEY.BROWSER_URL, array.get(holder.getAdapterPosition()).url);
                    v.performClick();
                case ACTION_CANCEL :
                    holder.tv_title.setMaxLines(1);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return dataArray.size();
    }

    /** ViewHolder for show data */
    class InfoViewHolder extends RecyclerView.ViewHolder {
        View view; // for click listener
        TextView tv_title, tv_date;

        InfoViewHolder(View view) {
            super(view);

            this.view = view.findViewById(R.id.card_info);
            this.tv_title= view.findViewById(R.id.card_info_title);
            this.tv_date = view.findViewById(R.id.card_info_date);
        }
    }
}

package kr.hee.kwnoti.u_campus_activity;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kr.hee.kwnoti.BrowserActivity;
import kr.hee.kwnoti.KEY;
import kr.hee.kwnoti.R;

/** 강의계획서 리스트에 사용되는 어댑터 */
class LectureListAdapter extends RecyclerView.Adapter<LectureListVH> {
    private ArrayList<LectureData> array;
    private Context context;

    LectureListAdapter(Context context) {
        array = new ArrayList<>();
        this.context = context;
    }

    /** 뷰 홀더를 inflate 시켜주는 메소드
     * @param parent      RecyclerView
     * @return            실체화된 {@link LectureListVH} 반환 */
    @Override public LectureListVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_lectures, parent, false); // 공지사항 레이아웃을 씀!
        return new LectureListVH(view);
    }

    @Override public void onBindViewHolder(final LectureListVH holder, final int position) {
        LectureData lectureData = array.get(position);
        final String subjName = lectureData.title;

        holder.division.setText(lectureData.division);
        holder.title.setText(lectureData.title);
        holder.score.setText(lectureData.score);
        holder.professor.setText(lectureData.prof);
        holder.number.setText(lectureData.number);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(context, BrowserActivity.class);
                intent.putExtra(KEY.BROWSER_FROM_LECTURE_NOTE, true);
                intent.putExtra(KEY.BROWSER_INCLUDE_URL, true);
                intent.putExtra(KEY.BROWSER_URL, array.get(holder.getAdapterPosition()).url);

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
    boolean addData(LectureData data) {
        if (data == null) return false;
        array.add(data);
        return true;
    }

    /** 어댑터 데이터를 지우는 메소드 */
    boolean cleanData() {
        if (array == null || array.size() == 0)
            return false;

        array = new ArrayList<>();
        return true;
    }
}

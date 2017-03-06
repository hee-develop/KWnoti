package kr.hee.kwnoti.food_activity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import kr.hee.kwnoti.R;

/** XML 뷰를 실체화 시키는 뷰 홀더 */
class FoodViewHolder extends RecyclerView.ViewHolder {
    TextView    dayOfWeek,  // 요일
                title,      // 학식 구분
                content;    // 학식

    public FoodViewHolder(View view) {
        super(view);
//        dayOfWeek = view.findViewById(R.id.)
        this.dayOfWeek = dayOfWeek;
        this.title = title;
        this.content = content;
    }
}

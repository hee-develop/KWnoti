package kr.hee.kwnoti.u_campus_activity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import kr.hee.kwnoti.R;

/** 강의계획서 리스트를 나타내기 위한 뷰 홀더 */
class LectureListVH extends RecyclerView.ViewHolder {
    View view; // 카드 자체
    TextView division, title, score, professor, number;

    // 생성자에서 뷰를 연결해줌
    LectureListVH(View view) {
        super(view);
        this.view = view.findViewById(R.id.card_lectures);
        division= (TextView)view.findViewById(R.id.card_lectures_division);
        title   = (TextView)view.findViewById(R.id.card_lectures_title);
        score   = (TextView)view.findViewById(R.id.card_lectures_score);
        professor = (TextView)view.findViewById(R.id.card_lectures_professor);
        number  = (TextView)view.findViewById(R.id.card_lectures_number);
    }
}

package kr.hee.kwnoti.u_campus_activity;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import kr.hee.kwnoti.R;

/** 유캠퍼스 메인화면의 RecyclerView XML 데이터 */
class UCampusMainViewHolder extends RecyclerView.ViewHolder {
    View view; // 클릭 리스너를 위해 선언된 카드 자체
    TextView title, room;
    ImageView newInfo, newAssignment;

    UCampusMainViewHolder(View view) {
        super(view);
        this.view   = view.findViewById(R.id.card_ucampus);
        title       = (TextView)view.findViewById(R.id.card_ucam_title);
        room        = (TextView)view.findViewById(R.id.card_ucam_room);
        newInfo     = (ImageView)view.findViewById(R.id.card_ucam_newNotice);
        newAssignment = (ImageView)view.findViewById(R.id.card_ucam_newAssignment);
    }
}

package kr.hee.kwnoti.tel_activity;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import kr.hee.kwnoti.R;

/** 그룹명에 해당하는 뷰 홀더 */
class GroupViewHolder extends RecyclerView.ViewHolder {
    TextView group;

    GroupViewHolder(View view) {
        super(view);
        group = (TextView)view.findViewById(R.id.header_groupName);
    }
}

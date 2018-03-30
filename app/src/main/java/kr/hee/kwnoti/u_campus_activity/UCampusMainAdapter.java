package kr.hee.kwnoti.u_campus_activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kr.hee.kwnoti.KEY;
import kr.hee.kwnoti.R;
import kr.hee.kwnoti.UTILS;

/** 유캠퍼스 메인화면의 RecyclerView Adapter */
class UCampusMainAdapter extends RecyclerView.Adapter<UCampusMainViewHolder> {
    private ArrayList<UCampusMainData> array;
    private Context context;

    UCampusMainAdapter(Context context) {
        array = new ArrayList<>();
        this.context = context;
    }

    /** 뷰 홀더를 inflate 시켜주는 메소드
     * @param parent      RecyclerView
     * @return            실체화된 UCampusMainViewHolder 반환 */
    @Override public UCampusMainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_ucam_main, parent, false);
        return new UCampusMainViewHolder(view);
    }

    /** 뷰 홀더에 데이터를 넣어 주는 메소드
     * @param holder      뷰 홀더
     * @param position    리사이클러 뷰에서의 위치 */
    @Override public void onBindViewHolder(final UCampusMainViewHolder holder, final int position) {
        UCampusMainData viewData = array.get(position);

        final String subjName = viewData.subjName;
        final String[] listType = {
                KEY.SUBJECT_PLAN, KEY.SUBJECT_INFO, KEY.SUBJECT_UTIL,
                KEY.SUBJECT_STUDENT, /*TODO 과제제출 비활성화 KEY.SUBJECT_ASSIGNMENT,*/ KEY.SUBJECT_QNA };

        holder.title.setText(subjName);
        holder.room.setText(viewData.subjPlace);
        if (viewData.newNotice) holder.newInfo.setVisibility(View.VISIBLE);
        if (viewData.newAssignment) holder.newAssignment.setVisibility(View.VISIBLE);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                UTILS.showAlertDialog(context, subjName, listType,
                        new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dInterface, int i) {
                                Intent intent = new Intent(context, UCampusListActivity.class);
                                intent.putExtra(KEY.SUBJECT, array.get(holder.getAdapterPosition()));
                                switch (i)
                                {
                                    case 0 : intent.putExtra(KEY.SUBJECT_LOAD_TYPE, listType[0]); break;
                                    case 1 : intent.putExtra(KEY.SUBJECT_LOAD_TYPE, listType[1]); break;
                                    case 2 : intent.putExtra(KEY.SUBJECT_LOAD_TYPE, listType[2]); break;
                                    case 3 : intent.putExtra(KEY.SUBJECT_LOAD_TYPE, listType[3]); break;
                                    case 4 : intent.putExtra(KEY.SUBJECT_LOAD_TYPE, listType[4]); break;
                                    case 5 : intent.putExtra(KEY.SUBJECT_LOAD_TYPE, listType[5]); break;
                                    default: intent.putExtra(KEY.SUBJECT_LOAD_TYPE, "Error"); break;
                                }
                                // 클릭한 과목에 해당하는 액티비티 생성
                                context.startActivity(intent);
                            }
                        }
                );
            }
        });
    }

    @Override public int getItemCount() {
        return array.size();
    }

    /** 다음장 데이터 추가를 위한 메소드.
     * 여기서 notifyDataSetChanged 메소드는 부르지 않음 (성능 향상을 위해)
     * @param data    다음장의 데이터
     * @return        제대로 추가가 되었는지 여부 */
    boolean addData(UCampusMainData data) {
        if (data == null) return false;
        array.add(data);
        return true;
    }
}

package kr.hee.kwnoti.settings_activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kr.hee.kwnoti.R;
import kr.hee.kwnoti.UTILS;

/** 푸쉬 필터 데이터를 출력해 주는 어댑터
 * DB는 {@link PushFilterDB} 클래스에서 관리함 */
class PushFilterAdapter extends RecyclerView.Adapter<PushFilterViewHolder> {
    private ArrayList<String> array;
    private PushFilterDB db;
    private Context context;

    /** 생성자
     * @param context    어댑터를 부른 액티비티 Context */
    PushFilterAdapter(Context context) {
        this.context = context;
        db = new PushFilterDB(context);
        array = new ArrayList<>();
        db.getFilters(array); // 파라미터 포인터로 가져옴
    }

    /** 뷰 홀더를 inflate 시켜주는 메소드
     * @param parent    RecyclerView
     * @return          실체화된 PushFilterViewHolder 리턴 */
    @Override public PushFilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_filter, parent, false);
        return new PushFilterViewHolder(view);
    }

    /** 생성된 ViewHolder 객체에 데이터를 불어 넣는 메소드
     * @param holder      'onCreateViewHolder'에서 만들어진 {@link PushFilterViewHolder}
     * @param position    'RecyclerView'에서의 위치(array 에서도 사용 가능) */
    @Override public void onBindViewHolder(final PushFilterViewHolder holder, int position) {
        // 카드 이름 부여
        holder.card_name.setText(array.get(position));
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                removeFilter(holder);
            }
        });
    }

    @Override public int getItemCount() {
        return array.size();
    }

    /** 필터 추가 및 화면에 표시 */
    boolean addFilter(String filter) {
        // 입력된 값이 없을 때
        if (filter.length() < 1) {
            UTILS.showToast(context, R.string.toast_enter_value);
            return false;
        }
        // 입력된 값이 30byte 초과일 때
        else if (filter.getBytes().length > 30) {
            UTILS.showToast(context, R.string.toast_tags_max_length_is);
            return false;
        }
        // 정상 입력 범위
        else {
            if (db.addFilter(filter)) {
                array.add(filter);
                notifyDataSetChanged();
                return true;
            }
            // 중복된 값이 존재할 때
            else {
                UTILS.showToast(context, R.string.toast_tags_same_found);
                return false;
            }
        }
    }

    /** 필터 제거 및 화면에서 제거 */
    private void removeFilter(PushFilterViewHolder holder) {
        String text = holder.card_name.getText().toString();
        if (db.removeFilter(text)) {
            UTILS.showToast(context, text + context.getString(R.string.toast_deleted));
            array.remove(holder.card_name.getText().toString());
            notifyDataSetChanged();
        }
    }
}
package kr.hee.kwnoti.food_activity;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kr.hee.kwnoti.R;

/** 학식 데이터를 출력해 주는 어댑터
 * DB는 {@link FoodDB} 클래스에서 관리함 */
class FoodAdapter extends RecyclerView.Adapter<FoodViewHolder> {
    private ArrayList<FoodData> array;
    private FoodDB db;

    /** 생성자
     * @param context    어댑터를 부른 액티비티 Context */
    FoodAdapter(Context context) {
        db = new FoodDB(context);
        array = new ArrayList<>();
        db.getData(array); // 파라미터 포인터로 가져옴
    }

    /** 뷰 홀더를 inflate 시켜주는 메소드
     * @param parent    RecyclerView
     * @return          실체화된 FoodViewHolder 리턴 */
    @Override public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_food, parent, false);
        return new FoodViewHolder(view);
    }

    /** 생성된 ViewHolder 객체에 데이터를 불어 넣는 메소드
     * @param holder      'onCreateViewHolder'에서 만들어진 {@link FoodViewHolder}
     * @param position    'RecyclerView'에서의 위치(array 에서도 사용 가능) */
    @Override public void onBindViewHolder(final FoodViewHolder holder, int position) {
        /*// 값 입력
        int     startMonth  = Integer.parseInt(array.get(position).startMonth),
                endMonth    = Integer.parseInt(array.get(position).endMonth),
                startDate   = Integer.parseInt(array.get(position).startDate),
                endDate     = Integer.parseInt(array.get(position).endDate);

        String month = (startMonth == endMonth) ? startMonth + "" : (startMonth + "~" + endMonth),
                date = (startDate == endDate) ? startDate + "" : (startDate + "~" + endDate);

        holder.month.setText(month + "월"); // TODO 추후 다국어 지원을 위한 변경
        holder.date.setText(date + "일");
        holder.content.setText(array.get(position).content);*/
    }

    @Override public int getItemCount() {
        return array.size();
    }

    /** 학식 데이터를 모두 없애는 메소드 */
    void cleanData() {
        db.cleanDB();
    }
}
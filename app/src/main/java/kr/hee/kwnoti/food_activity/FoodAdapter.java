package kr.hee.kwnoti.food_activity;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import kr.hee.kwnoti.R;

/** 학식 데이터를 출력해 주는 어댑터
 * DB는 {@link FoodDB} 클래스에서 관리함 */
class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private ArrayList<FoodData> dataArray;
    Context context;

    FoodAdapter(Context context, ArrayList<FoodData> foodArr) {
        this.context = context;
        this.dataArray = foodArr; // soft copy
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FoodViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_food, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodData foodData = dataArray.get(position);
        if (foodData == null) return;

        int dataLength = dataArray.size();

        String foodBfTitle;
        String foodBfTime;
        String foodBfPrice;
        String foodBfContent = null;

        String foodDinnerTitle;
        String foodDinnerTime;
        String foodDinnerPrice;
        String foodDinnerContent = null;

        FoodData fd;
        switch (dataLength) {
            case 2:
                fd = dataArray.get(1);
                foodBfTitle = fd.title;
//                foodBfTime  = fd.time + " ~ " + fd.endTime;
                foodBfPrice = fd.price;
                foodBfContent = fd.foodContents[position];
            case 1 :
                fd = dataArray.get(0);
                foodDinnerTitle = fd.title;
//                foodDinnerTime  = fd.time + " ~ " + fd.endTime;
                foodDinnerPrice = fd.price;
                foodDinnerContent = fd.foodContents[position];
        }

        holder.tv_breakfast.setText(foodBfContent);
        holder.tv_dinner.setText(foodDinnerContent);

//        holder.tv_dayOfWeek.setText(foodData.);


//        int     startMonth  = Integer.parseInt(array.get(position).startMonth),
//                endMonth    = Integer.parseInt(array.get(position).endMonth),
//                startDate   = Integer.parseInt(array.get(position).startDate),
//                endDate     = Integer.parseInt(array.get(position).endDate);
//
//        String month = (startMonth == endMonth) ? startMonth + "" : (startMonth + "~" + endMonth),
//                date = (startDate == endDate) ? startDate + "" : (startDate + "~" + endDate);
//
//        holder.month.setText(month + "월"); // TODO 추후 다국어 지원을 위한 변경
//        holder.date.setText(date + "일");
//        holder.content.setText(array.get(position).content);*/
    }

    @Override
    public int getItemCount() {
        return dataArray.size();
    }



    class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView tv_dayOfWeek, tv_breakfast, tv_dinner;

        public FoodViewHolder(View view) {
            super(view);

            this.tv_dayOfWeek = view.findViewById(R.id.card_food_day);
            this.tv_breakfast = view.findViewById(R.id.card_food_breakfast);
            this.tv_dinner = view.findViewById(R.id.card_food_dinner);
        }
    }



    /** 생성자
     * @paramd context    어댑터를 부른 액티비티 Context */
//    FoodAdadpter(Context context) {
//        db = new FoodDB(context);
//        array = new ArrayList<>();
//        db.getData(array); // 파라미터 포인터로 가져옴
//    }


    /** 생성된 ViewHolder 객체에 데이터를 불어 넣는 메소드
     * @param holder      'onCreateViewHolder'에서 만들어진 {@link FoodViewHolder}
     * @param position    'RecyclerView'에서의 위치(array 에서도 사용 가능) */
    public void onBindVfiewHolder(final FoodViewHolder holder, int position) {
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

    /** 학식 데이터를 모두 없애는 메소드 */
//    void cleanData() {
//        db.cleanDB();
//    }
}
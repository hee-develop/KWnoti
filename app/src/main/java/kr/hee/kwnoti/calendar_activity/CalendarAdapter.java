package kr.hee.kwnoti.calendar_activity;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import kr.hee.kwnoti.R;

/** 학사일정 데이터를 출력해 주는 어댑터
 * DB는 {@link CalendarDB} 클래스에서 관리함 */
class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private ArrayList<CalendarData> array;
    private CalendarDB db;
    // 오늘 날짜 기준의 객체 위치
    private int todayPosition = 0;

    /** 생성자
     * @param context    어댑터를 부른 액티비티 Context */
    CalendarAdapter(Context context) {
        db = new CalendarDB(context);
        array = new ArrayList<>();
        db.getData(array); // 파라미터 포인터로 가져옴

        db.closeDB();

        // 오늘 날짜를 받아 객체 위치를 찾음
        Calendar cal = new GregorianCalendar(Locale.KOREA);
        int todayMonth  = cal.get(Calendar.MONTH) + 1,
            todayDate   = cal.get(Calendar.DATE);
        for (int i = 0; i < array.size(); i++) {
            CalendarData data = array.get(i);
            int startMonth = Integer.parseInt(data.startMonth);
            int startDate = Integer.parseInt(data.startDate);

            if (startMonth < todayMonth) todayPosition = i;
            else if (startMonth == todayMonth && startDate < todayDate) todayPosition = i;
            else break;
        }
    }

    /** 뷰 홀더를 inflate 시켜주는 메소드
     * @param parent    RecyclerView
     * @return          실체화된 CalendarViewHolder 리턴 */
    @Override public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_calendar, parent, false);
        return new CalendarViewHolder(view);
    }

    /** 생성된 ViewHolder 객체에 데이터를 불어 넣는 메소드
     * @param holder      'onCreateViewHolder'에서 만들어진 {@link CalendarViewHolder}
     * @param position    'RecyclerView'에서의 위치(array 에서도 사용 가능) */
    @Override public void onBindViewHolder(final CalendarViewHolder holder, int position) {
        CalendarData data = array.get(position);

        // 값 입력
        int startMonth  = Integer.parseInt(data.startMonth),
            endMonth    = Integer.parseInt(data.endMonth),
            startDate   = Integer.parseInt(data.startDate),
            endDate     = Integer.parseInt(data.endDate);

        // 월/일 입력(만약 당일 이벤트가 아닌 경우 ~ 추가)
        String month = (startMonth == endMonth) ? startMonth + "" : (startMonth + "~" + endMonth),
                date = (startDate == endDate) ? startDate + "" : (startDate + "~" + endDate);

        // 데이터 설정
        holder.month.setText(month + "월"); // TODO 추후 다국어 지원을 위한 변경
        holder.date.setText(date + "일");
        holder.content.setText(data.content);
    }

    @Override public int getItemCount() {
        return array.size();
    }

    /** 오늘 날짜 기준의 객체 위치 반환 메소드
     * @return  객체 위치 */
    int getTodayPosition() {
        return todayPosition;
    }

    /** 학사 일정 데이터를 모두 없애는 메소드 */
    void cleanData(Context context) {
        db = new CalendarDB(context);
        db.cleanDB();
        db.closeDB();
    }
}
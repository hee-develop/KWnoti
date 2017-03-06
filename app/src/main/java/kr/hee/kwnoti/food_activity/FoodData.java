package kr.hee.kwnoti.food_activity;

/** 캘린더 데이터를 담는 클래스 */
class FoodData {
    String  dayOfWeek,  // 요일
            title,      // 조중식 혹은 석식
            content;    // 학식

    FoodData(String dayOfWeek, String title, String content) {
        this.dayOfWeek = dayOfWeek;
        this.title = title;
        this.content = content;
    }
}

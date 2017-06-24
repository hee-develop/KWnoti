package kr.hee.kwnoti.food_activity;

/** 캘린더 데이터를 담는 클래스 */
class FoodData {
    String type;    // 조식, 중식, 석식
    String price;   // 학식의 가격
    String startTime;   // 학식 운영시간
    String endTime;
    String[] contents;  // 학식

    FoodData(String type, String price, String startTime, String endTime, String[] contents) {
        this.type = type;
        this.price = price;
        this.startTime = startTime;
        this.endTime = endTime;
        this.contents = contents;
    }
}

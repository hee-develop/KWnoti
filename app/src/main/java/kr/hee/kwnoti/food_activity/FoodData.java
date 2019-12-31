package kr.hee.kwnoti.food_activity;

/**
 * Data class for food activity
 */
class FoodData {
    String title;
    String price;
    String time;
    String[] foodContents;

    FoodData(String type, String price, String time, String[] contents) {
        this.title = type;
        this.price = price;
        this.time = time;
        this.foodContents = contents;
    }
}

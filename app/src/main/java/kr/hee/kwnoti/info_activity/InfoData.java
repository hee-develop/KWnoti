package kr.hee.kwnoti.info_activity;

/** InfoData
 * A data class for information activity */
class InfoData {
    boolean isTopTitle;
    String title;
    String url;
    String date;

    InfoData(String title, String url, String date) {
        this.isTopTitle = false;
        this.title  = title;
        this.url    = url;
        this.date   = date;
    }
}
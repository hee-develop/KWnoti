package kr.hee.kwnoti.u_campus_activity;

/**
 * Created by ilmag on 2017-08-04.
 */

public class LectureData {
    public LectureData(String number, String title, String division, String score, String prof, String url) {
        this.number = number;
        this.title = title;
        this.division = division;
        this.score = score;
        this.prof = prof;
        this.url = url;
    }

    String number;  // 학정번호
    String title;   // 과목명
    String division;// 교과구분
    String score;   // 학점, 시간
    String prof;    // 교수
    String url;     // 강의계획서 주소
}

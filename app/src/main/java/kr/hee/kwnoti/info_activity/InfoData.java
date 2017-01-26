package kr.hee.kwnoti.info_activity;

/** InfoData
 * 뷰 홀더에 들어 갈 데이터들을 구분하기 쉽게 모아 둔 클래스 */
class InfoData {
    String title;   // 제목
    String whoWrite;// 작성 부서
    String date;    // 날짜
    String views;   // 조회수
    String link;    // 해당 글 주소
    boolean newInfo;// 새 글인지 여부
    boolean attachment; // 첨부파일 여부

    InfoData(String title, String whoWrite, String date,
             String views, String link, boolean newInfo, boolean attachment) {
        this.title      = title;
        this.whoWrite   = whoWrite;
        this.date       = date;
        this.views      = views;
        this.link       = link;
        this.newInfo    = newInfo;
        this.attachment = attachment;
    }
}
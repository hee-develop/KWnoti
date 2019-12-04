package kr.hee.kwnoti.info_activity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public abstract class RequestThread extends Thread {
    private final static int TIMEOUT = 10000;
    private Document doc;

    String url;

    public RequestThread(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        try {
            doc = Jsoup.connect(url).timeout(TIMEOUT).get();
        }
        catch (IOException e) {
            doc = null;
        }
        finally {
            afterReceived(doc);
        }
    }

    public abstract void afterReceived(Document doc);
}


//class ParserThread extends Thread {
//    static final int TIMEOUT = 10000;
//    boolean noWaitForResponse = false;
//
//    // 중단에 멈추도록 해주는 플래그
//    boolean stopLoop;
//
//    @Override public void run() {
//        super.run();
//
//        // 스레드 이름과 플래그 초기화
//        setName(THREAD_NAME);
//        stopLoop = false;
//
//        // 로딩 중 다이얼로그 표시
//        loadStart();
//
//        // 검색 기능 활성화 여부 확인 및 URL 생성
//        String baseUrl = "http://www.kw.ac.kr/ko/life/notice.do?mode=list&&articleLimit=10";
//
//        // 그룹의 종류에 따라 URL 결정
//        switch (groupType)
//        {
//            default : // 에러 시 전체로 검색
//            case "전체" : baseUrl += "&srCategoryId1="; break;
//            case "일반" : baseUrl += "&srCategoryId1=5"; break;
//            case "학사" : baseUrl += "&srCategoryId1=6"; break;
//            case "학생" : baseUrl += "&srCategoryId1=16"; break;
//            case "봉사" : baseUrl += "&srCategoryId1=17"; break;
//            case "등록/장학" : baseUrl += "&srCategoryId1=18"; break;
//            case "입학" : baseUrl += "&srCategoryId1=19"; break;
//            case "시설" : baseUrl += "&srCategoryId1=20"; break;
//            case "병무" : baseUrl += "&srCategoryId1=21"; break;
//            case "외부" : baseUrl += "&srCategoryId1=22"; break;
//        }
//
//        // 파서의 종류에 따라 URL 결정
//        switch (parserType)
//        {
//            case "제목" :
//                baseUrl += "&srSearchKey=article_title"; break;
//            case "내용" :
//                baseUrl += "&srSearchKey=article_text"; noWaitForResponse = true; break;
//            case "제목 + 내용" :
//                noWaitForResponse = true; break;
//            case "작성자" :
//                baseUrl += "&srSearchKey=writer_nm"; break;
//            default : break;
//        }
//
//        // 현재 페이지 설정
//        baseUrl += "&article.offset=" + currentPage;
//
//        // 검색 기능이 활성화된 경우 검색 문구 추가. URLEncoder 클래스 사용 불필요.
//        if (!parserType.equals("전체"))
//            baseUrl += "&srSearchVal=" + search_editText.getText().toString();
//
//        // 파싱 시작 =================================================
//        try {
//            Document doc;
//            if (noWaitForResponse)  doc = Jsoup.connect(baseUrl).get();
//            else                    doc = Jsoup.connect(baseUrl).timeout(TIMEOUT).get();
//
//            Elements elements = doc.select("li");
//            for (Element element : elements) {
//                // 멈추라는 신호가 있는 경우 중단
//                if (stopLoop) return;
//
//                // 상단 공지 출력 여부 결정(다음 쪽으로 넘어갈 땐 상단 공지 제거)
//                boolean isTopNotice = element.hasClass("top-notice");
//                if (isTopNotice && !showTopNotify) continue;
//
//                // 공지사항 선택
//                Element notify = element.select("div.board-text").first();
//                if (notify == null) continue; // 선택 오류가 났을 때 넘김
//
//                String  title       = notify.select("a").text(),
//                        content[]   = notify.select("p").text().split(" \\| ");
//                boolean isNewInfo   = title.contains("신규게시글"),
//                        hasAttachment = title.contains("Attachment");
//                // 제목 외 필요 없는 데이터 분리
//                title = title.replace("신규게시글", "").replace("Attachment", "");
//
//                // 분리한 데이터로 객체 생성 및 어댑터에 삽입
//                final InfoData infoData = new InfoData(title, content[2], content[1], content[0],
//                        "http://www.kw.ac.kr/ko/life/notice.do" + notify.select("a").attr("href"),
//                        isNewInfo, hasAttachment);
//                adapter.addInfo(infoData);
//            }
//
//            // 멈추라는 신호가 있는 경우 중단
//            if (stopLoop) return;
//
//            // 파싱이 끝나면 데이터가 변경됐음을 알림
//            runOnUiThread(new Runnable() {
//                @Override public void run() {
//                    adapter.notifyDataSetChanged();
//                }
//            });
//        }
//        catch (IOException e) {
//            // 대기시간이 초과됐거나 인터넷 연결이 안되있는 경우
//            e.printStackTrace();
//            runOnUiThread(new Runnable() {
//                @Override public void run() {
//                    Toast.makeText(InfoActivity.this, getString(R.string.toast_failed),
//                            Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//        finally {
//            loadFinish();
//        }
//    }
//}
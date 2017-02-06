package kr.hee.kwnoti;

/** 인자 전달 등에서 사용되는 String 값을 보기 편하게 만듦 */
public interface KEY {
    // SharedPreference key

    // FCM 디바이스 확인용 토큰
    String PREFERENCE_DEVICE_TOKEN = "Device Token";

    // 최초 실행 여부, true = 최초 실행임
    String FIRST_USE = "First Use";

    // 유캠퍼스 접속용 쿠키셋
    String COOKIE_SET = "Cookies";
}
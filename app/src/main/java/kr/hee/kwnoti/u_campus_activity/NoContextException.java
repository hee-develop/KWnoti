package kr.hee.kwnoti.u_campus_activity;

/** 유캠퍼스 Retrofit 객체를 만들 때 필요한 Context 객체가 없을 때 발생하는 예외에 대한 클래스 */
public final class NoContextException extends RuntimeException {
    NoContextException() {
        super();
    }
    NoContextException(String message) {
        super(message);
    }
}

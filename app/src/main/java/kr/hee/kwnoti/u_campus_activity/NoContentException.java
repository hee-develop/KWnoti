package kr.hee.kwnoti.u_campus_activity;

public class NoContentException extends Exception {
    NoContentException() {
        super("Content not found.");
    }
}

package kr.hee.kwnoti.u_campus_activity;

import android.os.Parcel;
import android.os.Parcelable;

/** 유캠퍼스 메인 액티비티의 RecyclerView 데이터 */
class UCampusMainData implements Parcelable {
    String subjName;     // 과목명
    String subjPlace;    // 강의실 위치
    // 강의 고유 데이터
    String subjId;       // 강의 고유번호
    String subjYear;     // 강의 년도
    String subjTerm;     // 강의 학기
    String subjClass;    // 강의 분반

    boolean newNotice = false;      // 새 공지사항 여부
    boolean newAssignment = false;  // 새 과제 여부

    UCampusMainData() { }

    private UCampusMainData(Parcel in) {
        subjName = in.readString();
        subjPlace = in.readString();
        subjId = in.readString();
        subjYear = in.readString();
        subjTerm = in.readString();
        subjClass = in.readString();
        newNotice = in.readByte() != 0;
        newAssignment = in.readByte() != 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subjName);
        dest.writeString(subjPlace);
        dest.writeString(subjId);
        dest.writeString(subjYear);
        dest.writeString(subjTerm);
        dest.writeString(subjClass);
        dest.writeByte((byte) (newNotice ? 1 : 0));
        dest.writeByte((byte) (newAssignment ? 1 : 0));
    }

    @Override public int describeContents() {
        return 0;
    }

    public static final Creator<UCampusMainData> CREATOR = new Creator<UCampusMainData>() {
        @Override public UCampusMainData createFromParcel(Parcel in) {
            return new UCampusMainData(in);
        }

        @Override public UCampusMainData[] newArray(int size) {
            return new UCampusMainData[size];
        }
    };
}

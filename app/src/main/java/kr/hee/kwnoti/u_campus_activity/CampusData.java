package kr.hee.kwnoti.u_campus_activity;

import java.util.ArrayList;

public class CampusData {
    private String subName;
    private ArrayList<String> subTime;
    private ArrayList<String> subUrlData;

    public CampusData(String subName, ArrayList<String> subTime, ArrayList<String> subUrlData) {
        this.subName = subName;
        this.subTime = subTime;
        this.subUrlData = subUrlData;
    }

    public String getSubName() {
        return subName;
    }

    public String getSubTime() {
        StringBuilder sb = new StringBuilder();
        for (String str : subTime)
            sb.append(str).append(", ");
        sb.delete(sb.length()-2, sb.length()-1);
        return sb.toString();
    }

    public ArrayList<String> getSubUrlData() {
        return subUrlData;
    }
}

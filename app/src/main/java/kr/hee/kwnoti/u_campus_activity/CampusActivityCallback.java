package kr.hee.kwnoti.u_campus_activity;

import java.util.ArrayList;

public interface CampusActivityCallback {
    void onLoginFinished();
    void loadCampusData(String campusHtml);
    void onLoadFinished(ArrayList<CampusData> arrayList);
}

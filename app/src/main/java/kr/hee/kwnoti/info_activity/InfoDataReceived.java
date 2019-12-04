package kr.hee.kwnoti.info_activity;

import java.util.ArrayList;

public interface InfoDataReceived {
    /**
     * Make list view using array data
     * @param infoArr the data that received by parser
     */
    void makeView(ArrayList infoArr);
}

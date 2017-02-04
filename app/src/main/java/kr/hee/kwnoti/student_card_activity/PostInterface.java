package kr.hee.kwnoti.student_card_activity;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

interface PostInterface {
    String URL = "http://mobileid.kw.ac.kr/mobile/MA/xml_userInfo.php/";

    // POST 요청으로 Response body 받는 메소드
    @FormUrlEncoded @POST(URL)
    Call<PostResult> getBody(
            @Field("real_id") String id,
            @Field("new_check") String check);
}

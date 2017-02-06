package kr.hee.kwnoti.u_campus_activity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface UCampusInterface {
    String URL = "http://info2.kw.ac.kr/servlet/controller.homepage.MainServlet/";

    @GET(URL)
    Call<ResponseBody> getBody(
            @Query("member_no") String id,
            @Query("password") String password,
            @Query("login_type") String type,
            @Query("redirect_url") String url,
            @Query("gubun_code") String code);


    @GET("/")
    Call<ResponseBody> getMain();

/*
    login_type=2&redirect_url=http%3A%2F%2Finfo.kw.ac.kr%2F&layout_opt=&gubun_code=11&
    p_language=KOREAN&image.x=19&image.y=18&
    p_gate=univ&p_process=main&p_page=learning&p_kwLoginType=cookie&gubun_code=11"*/
}

/*

        // POST 요청으로 Response body 받는 메소드
        @FormUrlEncoded @POST(URL)
        Call<PostResult> getBody(
                @Field("real_id") String id,
                @Field("new_check") String check);
*/

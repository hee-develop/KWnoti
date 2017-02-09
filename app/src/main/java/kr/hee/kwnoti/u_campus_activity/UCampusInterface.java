package kr.hee.kwnoti.u_campus_activity;

import android.support.annotation.Nullable;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/** 최초 로그인 시 사용하는 인터페이스
 * POST 로그인을 사용하며 Response 에서 쿠키값만 가져오는 용도로 사용한다 */
interface UCampusInterface {
    String URL = "https://info.kw.ac.kr/";
    @FormUrlEncoded @POST("webnote/login/login_proc.php")
    Call<ResponseBody> getCookie(
            @Field("login_type")            String login_type,
            @Field("redirect_url")          String redirect_url,
            @Field("gubun_code")            String gubun_code,  // 사용자 유형(학생은 11)
            @Field("layout_opt") @Nullable  String layout_opt,  // ""
            @Field("p_language")            String language,
            @Field("member_no")             String id,          // 학번
            @Field("password")              String pwd          // 비밀번호
    );


    @GET("http://info2.kw.ac.kr/servlet/controller.homepage.MainServlet?p_gate=univ&p_process=main&p_page=learning&p_kwLoginType=cookie&gubun_code=11")
    Call<ResponseBody> getMain();
}
/*
    String URL = "http://info2.kw.ac.kr/servlet/controller.homepage.MainServlet/";

    @GET("/")
    Call<ResponseBody> getMain();

    login_type=2&redirect_url=http%3A%2F%2Finfo.kw.ac.kr%2F&layout_opt=&gubun_code=11&
    p_language=KOREAN&image.x=19&image.y=18&
    p_gate=univ&p_process=main&p_page=learning&p_kwLoginType=cookie&gubun_code=11"

        // POST 요청으로 Response body 받는 메소드
        @FormUrlEncoded @POST(URL)
        Call<PostResult> getBody(
                @Field("real_id") String id,
                @Field("new_check") String check);
*/

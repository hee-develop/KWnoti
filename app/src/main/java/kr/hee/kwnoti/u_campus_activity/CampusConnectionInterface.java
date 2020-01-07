package kr.hee.kwnoti.u_campus_activity;

import androidx.annotation.Nullable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface CampusConnectionInterface {
    // login for u-campus
    @FormUrlEncoded
    @POST("webnote/login/login_proc.php")
    Call<ResponseBody> loginToCampus(
            @Field("login_type")   String login_type,
            @Field("redirect_url") String redirect_url,
            @Field("gubun_code")   String gubun_code,
            @Field("layout_opt") @Nullable String layout_opt,
            @Field("p_language")   String language,
            @Field("member_no")    String id,
            @Field("password")     String pwd
    );

    // load u-campus
    @GET("https://info2.kw.ac.kr/servlet/controller.homepage.MainServlet?p_gate=univ&p_process=main&p_page=learning&p_kwLoginType=cookie&gubun_code=11")
    Call<ResponseBody> loadCampus();

    // load student data
    @GET("https://info2.kw.ac.kr/servlet/controller.homepage.KwuMainServlet?p_process=openStu&")
    Call<ResponseBody> loadStudentData();
}

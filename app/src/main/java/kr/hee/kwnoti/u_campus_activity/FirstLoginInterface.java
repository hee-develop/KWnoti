package kr.hee.kwnoti.u_campus_activity;

import android.support.annotation.Nullable;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/** 최초 로그인 시 사용하는 인터페이스
 * POST 로그인을 사용하며 Response 에서 쿠키값만 가져오는 용도로 사용한다 */
interface FirstLoginInterface {
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
}
package kr.hee.kwnoti.wifi_activity;

import android.support.annotation.Nullable;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/** 와이파이 아이디 요청을 위한 인터페이스 */
public interface WifiInterface {
    /* 동시에 4개의 페이지를 검증을 마치고 진행해야하는건지 아니며 하나씩 차례 차례 해도 괜찮은건지 + 쿠키를 받아서 헤더에 추가하는 방법 + 헤더 추가하는 방법 */

    /*---------------와이파이발급1단계-----------------*/
    String UCAM_CHECK_URL = "http://info.kw.ac.kr/";
    @FormUrlEncoded @POST("webnote/wireless/nespot_user_check.php")
    Call<ResponseBody> checkLogin(
            @Field("user_gubun")			String user_gubun,
            @Field("user_id")				String user_id,
            @Field("user_passwd")			String user_passwd,
            @Field("x")						String x,
            @Field("y")						String y
    );

    /*---------------와이파이발급2단계-----------------*/
    String NESPOT_LOGIN_URL = "http://midbas.nespot.com/";
    @FormUrlEncoded @POST("user/login_proc_get.asp")
    Call<ResponseBody> getNespotLogin(
            @Field("bt_submit")				String bt_submit,
            @Field("coid")					String coid,
            @Field("department")			String department,
            @Field("domain")				String domain,
            @Field("name")					String name,
            @Field("position")				String position,
            @Field("usergbn1")				String usergbn1,
            @Field("usergbn2")				String usergbn2
    );

    /*---------------와이파이발급3단계-----------------*/
    String OLLEH_LOGIN_URL = "http://midbas.wifi.olleh.com/";
    @FormUrlEncoded @POST("user/login_proc_get.asp")
    Call<ResponseBody> getOllehLogin(
            @Field("coid")					String coid,
            @Field("department")			String department,
            @Field("deptnm")				String deptnm,
            @Field("domain")				String domain,
            @Field("name")					String name,
            @Field("positoin")				String positoin,
            @Field("usergbn1")				String usergbn1,
            @Field("usergbn2")				String usergbn2
    );

    /*---------------와이파이발급4단계-----------------*/
    String INDEX_URL = "http://midbas.wifi.olleh.com/";
    @FormUrlEncoded @POST("user/index_get.asp")
    Call<ResponseBody> getIndex(
            @Field("domain")				String domain,
            @Field("name")					String name,
            @Field("user_gbn")				String user_gbn,
            @Field("userid")				String userid
    );

    /*---------------와이파이 데이터 로드-----------------*/
    String WIFI_URL = "http://midbas.wifi.olleh.com/";
    @GET("http://midbas.wifi.olleh.com/user/join_info/user_reg.asp")
    Call<ResponseBody> getWifi();
}
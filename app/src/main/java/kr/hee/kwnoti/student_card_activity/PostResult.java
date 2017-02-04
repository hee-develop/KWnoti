package kr.hee.kwnoti.student_card_activity;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root
class PostResult {
    @Path("item") @Element(name = "result_code")
    public String resultCode;
    @Path("item") @Element(name = "result_msg")
    public String resultMsg;
    @Path("item") @Element(name = "user_code")
    public String userCode;
    @Path("item") @Element(name = "user_patCode")
    public String userPatCode;
    @Path("item") @Element(name = "user_patName")
    public String userPatName;
    @Path("item") @Element(name = "user_deptCode")
    public String userDeptCode;
    @Path("item") @Element(name = "user_deptName")
    public String userDeptName;
    @Path("item") @Element(name = "user_name")
    public String userName;
    @Path("item") @Element(name = "user_photoUrl", required = false)
    public String userPhotoUrl;
    @Path("item") @Element(name = "qr_code")
    public String qrCode;
    @Path("item") @Element(name = "sysdate")
    public String sysDate;
}

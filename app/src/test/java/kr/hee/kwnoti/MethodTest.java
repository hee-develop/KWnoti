package kr.hee.kwnoti;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

public class MethodTest {
    public boolean checkPwd(String pwd) {
        boolean passwordIdOk;

        // 글자 수가 6~8 자리인 경우에만 true 반환
        // 글자가 숫자와 영문이 모두 포함됐을 때만 true 반환
//        String regex = "/[$\\\\!\\@\\#\\%\\\\^\\&\\*\\(\\)\\[\\]\\+\\_\\{\\}\\`\\~\\=\\|]/";
        String regex = "[a-zA-Z0-9]+";
//        String regex = "^[a-zA-Z0-9]+$";
//        String regex = "/^[a-z|A-Z|0-9]+$/g";
        passwordIdOk = Pattern.matches(regex, pwd);
//        Pattern p = Pattern.compile(regex);
//        Matcher m = p.matcher(pwd);
//                && (pwd.length() >= 6 && pwd.length() <= 8);

        return passwordIdOk;
//        return m.find();
    }
    

    @Test public void test() throws Exception {
        assertEquals(true, checkPwd("abcd12"));
        assertEquals(false, checkPwd("abcd12@"));
        assertEquals(false, checkPwd("@abcd12"));
        assertEquals(false, checkPwd("@abcd12@"));
        assertEquals(false, checkPwd("abcd12@2"));
        assertEquals(false, checkPwd("abcd12@a"));
        assertEquals(false, checkPwd("@abcd12@a"));
        assertEquals(false, checkPwd("@abcd12@2"));
        assertEquals(false, checkPwd("11AbCd"));
        assertEquals(false, checkPwd("11AbCd@"));
        assertEquals(false, checkPwd("abcd12!"));
        assertEquals(false, checkPwd("abcd@@!"));
        assertEquals(false, checkPwd("!abcd@@!"));
        assertEquals(false, checkPwd("!231abcd@@!"));
        assertEquals(false, checkPwd("231abcd@@!"));
        assertEquals(false, checkPwd("23@abcd@!"));
    }

}
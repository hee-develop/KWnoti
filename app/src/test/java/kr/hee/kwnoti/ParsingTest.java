package kr.hee.kwnoti;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

public class ParsingTest {
    private String select() {
        String html = "\n" +
                "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n" +
                "<html>\n" +
                "<head>\n" +
                "<title>광운대학교 종합정보서비스 :: 강의계획서 조회</title>\n" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">\n" +
                "<link rel=\"shortcut icon\" href=\"/images/kw_favicon.ico\">\n" +
                "<link href=\"/style/style.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
                "<script type=\"text/javascript\">document.domain=\"kw.ac.kr\";</script>\n" +
                "<script src=\"/style/common.js\" language=\"JavaScript\"></script>\n" +
                "<script type=\"text/javascript\">\n" +
                "<!--\n" +
                "function fsel_tit1 () {\n" +
                "  document.myform.fsel2.options[0].selected=true;\n" +
                "  //document.myform.fsel4.options[0].selected=true;\n" +
                "  if(typeof(document.myform.fsel3) != 'undefined') document.myform.fsel3.options[0].selected=true;\n" +
                "}\n" +
                "\n" +
                "function fsel_tit2 () {\n" +
                "  form = document.myform;\n" +
                "  form.fsel1.options[0].selected=true;\n" +
                "  if(typeof(form.fsel3) != 'undefined') form.fsel3.options[0].selected=true;\n" +
                "\n" +
                "  major_iframe.location.href = 'set_major.php?layout_opt=N&hakgwa_code='+form.fsel2.value.substring(0, 3);\n" +
                "}\n" +
                "\n" +
                "function fsel_tit3(){\n" +
                "  document.myform.fsel1.options[0].selected=true;\n" +
                "  document.myform.fsel2.options[0].selected=true;\n" +
                "  //document.myform.fsel4.options[0].selected=true;\n" +
                "}\n" +
                "\n" +
                "// 년도학기 변경시 학과/전공코드 재생성하기 위하여 submit\n" +
                "// 교수,강사외 수강신청기간중 인증코드 필요 sycho 2016.02.16\n" +
                "function func_reload(){\n" +
                "    /*******************************************************\n" +
                "  if(document.myform.captcha.value == ''){\n" +
                "    alert('인증코드를 입력해주세요'); \n" +
                "    document.myform.captcha.focus();\n" +
                "    return false;\n" +
                "  }\n" +
                "  *********************************************************/\n" +
                "    document.myform.mode.value = '';    //학과/전공코드 갱신시 목록 조회 안함 sycho 2016.03.07\n" +
                "  document.myform.submit();\n" +
                "}\n" +
                "\n" +
                "\n" +
                "// 교수,강사외 수강신청기간중 인증코드 필요 sycho 2016.02.16\n" +
                "function func_submit(){\n" +
                "    /*******************************************************/\n" +
                "  if(document.myform.captcha.value == ''){\n" +
                "    alert('인증코드를 입력해주세요'); \n" +
                "    document.myform.captcha.focus();\n" +
                "    return false;\n" +
                "  }\n" +
                "  /*********************************************************/\n" +
                "    document.myform.submit();\n" +
                "}\n" +
                "//-->\n" +
                "</script>\n" +
                "\n" +
                "\n" +
                "<script type=\"text/javascript\">\n" +
                "<!--\n" +
                "// 외부 iframe 자동 리사이즈\n" +
                "var ifrContentsTimer;\n" +
                "\n" +
                "// 이미지 등 로딩시간이 걸리는 것들이 로딩된 후 다시 한번 리사이즈\n" +
                "function resizeRetry(){\n" +
                "  if(document.body.readyState == \"complete\"){\n" +
                "    clearInterval(ifrContentsTimer);\n" +
                "  }\n" +
                "  else{\n" +
                "    resizeFrame();\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "// 페이지가 로딩되면 바로 리사이즈\n" +
                "function resizeFrame(){\n" +
                "  self.resizeTo(document.body.scrollWidth + (document.body.offsetWidth-document.body.clientWidth), parseInt(document.body.scrollHeight)+10);\n" +
                "}\n" +
                "\n" +
                "window.onload=function(){\n" +
                "  resizeFrame();\n" +
                "  ifrContentsTimer = setInterval(\"resizeRetry()\", 100);\n" +
                "}\n" +
                "\n" +
                "//-->\n" +
                "</script>\n" +
                "\n" +
                "\n" +
                "</head>\n" +
                "\n" +
                "<body style=\"margin:0px;\">\n" +
                "\n" +
                "\n" +
                "<!-- 본문 시작 -->\n" +
                "\n" +
                "<iframe name='major_iframe' width='0' height='0'></iframe>\n" +
                "\n" +
                "<table align='center' border=0 cellspacing=0 width=750>\n" +
                "  <tr>\n" +
                "    <td><img src=\"/webnote/images/s_title_03.gif\"></td>\n" +
                "  </tr>\n" +
                "</table><br>\n" +
                "\n" +
                "<table align='center' width=750>\n" +
                "  <tr>\n" +
                "    <td>\n" +
                "      <input type=\"radio\" name=\"lecture_opt\" id=\"lecture_opt_h\" checked><b><font color=\"#0000FF\"><label for=\"lecture_opt_h\" style=\"cursor:hand\">학부</label></font></b> &nbsp;\n" +
                "      <input type=\"radio\" name=\"lecture_opt\" id=\"lecture_opt_d\" onclick=\"javascript:location.href='h_lecture_d.php?layout_opt=N&skin_opt=';\"><label for=\"lecture_opt_d\" style=\"cursor:hand\">대학원</label>\n" +
                "    </td>\n" +
                "  </tr>\n" +
                "</table>\n" +
                "\n" +
                "<form method='post' name='myform' action=\"/webnote/lecture/h_lecture.php\" style='margin:5;'>\n" +
                "  <input type=hidden name=mode value=view>\n" +
                "  <input type=hidden name=user_opt value=>\n" +
                "  <input type=hidden name=skin_opt value=>\n" +
                "  <input type=hidden name=layout_opt value=N>\n" +
                "  <input type='hidden' name='show_hakbu' value=''>\n" +
                "\n" +
                "<table align='center' border=1 cellspacing=0 width=750 cellpadding='3' bordercolorlight='#CCCCCC' bordercolordark='#FFFFFF'>\n" +
                "  <colspan>\n" +
                "    <col width=125>\n" +
                "    <col width=250>\n" +
                "    <col width=125>\n" +
                "    <col width=250>\n" +
                "  </colspan>\n" +
                "  <tr>\n" +
                "    <td align=center class='bgtable1'><b>년도/학기</b></td>\n" +
                "    <td>\n" +
                "      <select name=\"this_year\" onchange=\"javascript:func_reload();\">\n" +
                "                <option value=\"2017\" selected>2017 년</option>\n" +
                "                <option value=\"2016\" >2016 년</option>\n" +
                "                <option value=\"2015\" >2015 년</option>\n" +
                "                <option value=\"2014\" >2014 년</option>\n" +
                "                <option value=\"2013\" >2013 년</option>\n" +
                "                <option value=\"2012\" >2012 년</option>\n" +
                "                <option value=\"2011\" >2011 년</option>\n" +
                "                <option value=\"2010\" >2010 년</option>\n" +
                "                <option value=\"2009\" >2009 년</option>\n" +
                "                <option value=\"2008\" >2008 년</option>\n" +
                "                <option value=\"2007\" >2007 년</option>\n" +
                "                <option value=\"2006\" >2006 년</option>\n" +
                "                <option value=\"2005\" >2005 년</option>\n" +
                "                <option value=\"2004\" >2004 년</option>\n" +
                "                <option value=\"2003\" >2003 년</option>\n" +
                "                <option value=\"2002\" >2002 년</option>\n" +
                "                <option value=\"2001\" >2001 년</option>\n" +
                "                <option value=\"2000\" >2000 년</option>\n" +
                "                <option value=\"1999\" >1999 년</option>\n" +
                "                <option value=\"1998\" >1998 년</option>\n" +
                "              </select> &nbsp;\n" +
                "      <select name='hakgi' onchange=\"javascript:func_reload();\">\n" +
                "        <option value='1' >1 학기</option>\n" +
                "        <option value='2' selected>2 학기</option>\n" +
                "        <option value='3'>여름 학기</option>\n" +
                "        <option value='4'>겨울 학기</option>\n" +
                "      </select>\n" +
                "    </td>\n" +
                "\n" +
                "        <td align=center class='bgtable1'><b>수강여부</b></td>\n" +
                "    <td>\n" +
                "      <input type=\"radio\" name=\"sugang_opt\" id=\"sugang_opt_all\" value=\"all\" checked><label for=\"sugang_opt_all\" style=\"cursor:hand\">전체</label> &nbsp;\n" +
                "      <input type=\"radio\" name=\"sugang_opt\" id=\"sugang_opt_my\" value=\"my\" ><label for=\"sugang_opt_my\" style=\"cursor:hand\">수강과목</label>\n" +
                "    </td>\n" +
                "    \n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td align=center class='bgtable1'><b>과목명</b></td>\n" +
                "    <td><input size=20 maxlength=40 type='text' name='hh' value=''></td>\n" +
                "    <td align=center class='bgtable1'><b>담당교수</b></td>\n" +
                "    <td><input size=20 maxlength=40 type='text' name='prof_name' value=''></td>\n" +
                "  </tr>\n" +
                "<!--\n" +
                "  -->\n" +
                "  <!-- 공통과목 조회 -->\n" +
                "  <tr>\n" +
                "    <td align=center class='bgtable1'><b>공통과목\n" +
                "    <td colspan=3>\n" +
                "      <select name='fsel1' onChange='javascript:fsel_tit1()'>\n" +
                "        <option value='00_00'>- 전체 -</option>\n" +
                "        <option value='0000_전체공통' >전체공통</option>\n" +
                "\n" +
                "                <option value=\"1000_공과대학\" >공과대학</option>\n" +
                "                <option value=\"3000_인문사회과학대학\" >인문사회과학대학</option>\n" +
                "                <option value=\"4000_법과대학\" >법과대학</option>\n" +
                "                <option value=\"5000_경영대학\" >경영대학</option>\n" +
                "                <option value=\"6000_자연과학대학\" >자연과학대학</option>\n" +
                "                <option value=\"7000_전자정보공과대학\" >전자정보공과대학</option>\n" +
                "                <option value=\"9000_사회과학대학\" >사회과학대학</option>\n" +
                "                <option value=\"A000_교양학부\" >교양학부</option>\n" +
                "                <option value=\"B000_학점은행대학\" >학점은행대학</option>\n" +
                "                <option value=\"C000_국어국문학부\" >국어국문학부</option>\n" +
                "                <option value=\"D000_동북아대학\" >동북아대학</option>\n" +
                "                <option value=\"E000_인문대학\" >인문대학</option>\n" +
                "                <option value=\"F000_정책법학대학\" >정책법학대학</option>\n" +
                "                <option value=\"G000_인제니움학부대학\" >인제니움학부대학</option>\n" +
                "                <option value=\"H000_소프트웨어융합대학\" >소프트웨어융합대학</option>\n" +
                "                <option value=\"X000_연계전공\" >연계전공</option>\n" +
                "        \n" +
                "      </select>\n" +
                "    </td>\n" +
                "  </tr>\n" +
                "\n" +
                "  <!-- 학과별 조회 -->\n" +
                "  <tr>\n" +
                "    <td align=center class='bgtable1'><b>학과/전공\n" +
                "    <td colspan=3>\n" +
                "      <select name='fsel2' onChange='javascript:fsel_tit2()'>\n" +
                "        <option value='00_00' selected>- 전체 -</option>\n" +
                "\n" +
                "                <option value=\"0000_전체공통 대학공통\" >전체공통 대학공통</option>\n" +
                "                <option value=\"1140_공과대학 화학공학과\" >공과대학 화학공학과</option>\n" +
                "                <option value=\"1160_공과대학 환경공학과\" >공과대학 환경공학과</option>\n" +
                "                <option value=\"1170_공과대학 건축공학과\" >공과대학 건축공학과</option>\n" +
                "                <option value=\"1270_공과대학 건축학과\" >공과대학 건축학과</option>\n" +
                "                <option value=\"3000_인문사회과학대학 대학공통\" >인문사회과학대학 대학공통</option>\n" +
                "                <option value=\"3040_인문사회과학대학 국어국문학과\" >인문사회과학대학 국어국문학과</option>\n" +
                "                <option value=\"3110_인문사회과학대학 산업심리학과\" >인문사회과학대학 산업심리학과</option>\n" +
                "                <option value=\"3170_인문사회과학대학 미디어영상학부\" >인문사회과학대학 미디어영상학부</option>\n" +
                "                <option value=\"3210_인문사회과학대학 동북아문화산업학부\" >인문사회과학대학 동북아문화산업학부</option>\n" +
                "                <option value=\"4050_법과대학 법학부\" >법과대학 법학부</option>\n" +
                "                <option value=\"4100_법과대학 자산관리학과\" >법과대학 자산관리학과</option>\n" +
                "                <option value=\"5080_경영대학 경영학부\" >경영대학 경영학부</option>\n" +
                "                <option value=\"5100_경영대학 국제통상학부\" >경영대학 국제통상학부</option>\n" +
                "                <option value=\"6000_자연과학대학 대학공통\" >자연과학대학 대학공통</option>\n" +
                "                <option value=\"6030_자연과학대학 수학과\" >자연과학대학 수학과</option>\n" +
                "                <option value=\"6050_자연과학대학 화학과\" >자연과학대학 화학과</option>\n" +
                "                <option value=\"6090_자연과학대학 생활체육학과\" >자연과학대학 생활체육학과</option>\n" +
                "                <option value=\"6100_자연과학대학 전자바이오물리학과\" >자연과학대학 전자바이오물리학과</option>\n" +
                "                <option value=\"6120_자연과학대학 정보콘텐츠학과\" >자연과학대학 정보콘텐츠학과</option>\n" +
                "                <option value=\"7000_전자정보공과대학 대학공통\" >전자정보공과대학 대학공통</option>\n" +
                "                <option value=\"7060_전자정보공과대학 전자공학과\" >전자정보공과대학 전자공학과</option>\n" +
                "                <option value=\"7070_전자정보공과대학 전자통신공학과\" >전자정보공과대학 전자통신공학과</option>\n" +
                "                <option value=\"7220_전자정보공과대학 컴퓨터공학과\" >전자정보공과대학 컴퓨터공학과</option>\n" +
                "                <option value=\"7260_전자정보공과대학 컴퓨터소프트웨어학과\" >전자정보공과대학 컴퓨터소프트웨어학과</option>\n" +
                "                <option value=\"7320_전자정보공과대학 전기공학과\" >전자정보공과대학 전기공학과</option>\n" +
                "                <option value=\"7340_전자정보공과대학 전자재료공학과\" >전자정보공과대학 전자재료공학과</option>\n" +
                "                <option value=\"7410_전자정보공과대학 로봇학부\" >전자정보공과대학 로봇학부</option>\n" +
                "                <option value=\"7420_전자정보공과대학 전자융합공학과\" >전자정보공과대학 전자융합공학과</option>\n" +
                "                <option value=\"9050_사회과학대학 산업심리학과\" >사회과학대학 산업심리학과</option>\n" +
                "                <option value=\"D000_동북아대학 동북아대학\" >동북아대학 동북아대학</option>\n" +
                "                <option value=\"D020_동북아대학 동북아통상학부\" >동북아대학 동북아통상학부</option>\n" +
                "                <option value=\"D030_동북아대학 동북아문화산업학부\" >동북아대학 동북아문화산업학부</option>\n" +
                "                <option value=\"E020_인문대학 국어국문학과\" >인문대학 국어국문학과</option>\n" +
                "                <option value=\"F030_정책법학대학 법학부\" >정책법학대학 법학부</option>\n" +
                "                <option value=\"F040_정책법학대학 국제학부\" >정책법학대학 국제학부</option>\n" +
                "                <option value=\"F050_정책법학대학 자산관리학과\" >정책법학대학 자산관리학과</option>\n" +
                "                <option value=\"H000_소프트웨어융합대학 대학공통\" >소프트웨어융합대학 대학공통</option>\n" +
                "                <option value=\"H020_소프트웨어융합대학 컴퓨터정보공학부\" >소프트웨어융합대학 컴퓨터정보공학부</option>\n" +
                "                <option value=\"H030_소프트웨어융합대학 소프트웨어학부\" >소프트웨어융합대학 소프트웨어학부</option>\n" +
                "                <option value=\"H040_소프트웨어융합대학 정보융합학부\" >소프트웨어융합대학 정보융합학부</option>\n" +
                "              </select>\n" +
                "\n" +
                "      <select name='fsel4' style='width:150;'>\n" +
                "        <option value='00_00'>- 전체 -</option>\n" +
                "\n" +
                "              </select>\n" +
                "    </td>\n" +
                "  </tr>\n" +
                "\n" +
                "  \n" +
                "    <!---------------------------------------------------------------->\n" +
                "  <tr>\n" +
                "    <td align='center' class='bgtable1'><b>인증코드</b></td>\n" +
                "    <td colspan='3'><img src=\"captcha.php\" />&nbsp;&nbsp;<input name=\"captcha\" type=\"text\" maxlength='4' size='5'>\n" +
                "    ※ 시스템 과부하 방지를 위하여 인증코드를 입력해주시기 바랍니다.\n" +
                "    </td>\n" +
                "  </tr>\n" +
                " <!----------------------------------------------------------------->\n" +
                "  \n" +
                "</table><br>\n" +
                "\n" +
                "<table align='center' width=750>\n" +
                "  <tr>\n" +
                "    <td align=center>\n" +
                "      <input type=\"image\" src='/webnote/abeek/images/btn_search.gif' onclick=\"javascript:return func_submit();\"> &nbsp;\n" +
                "      <img src='/webnote/abeek/images/btn_cancel.gif' onclick='document.myform.reset();' style='cursor:hand;'></a>\n" +
                "    </td>\n" +
                "  </tr>\n" +
                "</table>\n" +
                "\n" +
                "</form>\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "<script type='text/javascript'>\n" +
                "// 이전화면에서의 학기 선택값을 유지하기 위한 스크립트\n" +
                "document.myform.hakgi.selectedIndex = 2 - 1;\n" +
                "</script>\n" +
                "\n" +
                "<!-- 본문 끝 -->\n" +
                "\n" +
                "\n" +
                "</body>\n" +
                "</html>";

        Document doc = Jsoup.parse(html);

        Elements elements = doc.select("select[name=this_year] > option");



        return null;
    }


    @Test public void test() throws Exception {
        assertEquals(select(), "2017");
    }

}
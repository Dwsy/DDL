package link.dwsy.ddl.core.utils;

import cn.hutool.http.HtmlUtil;

/**
 * @Author Dwsy
 * @Date 2022/9/10
 */

public class HTMLUtil {

    /**
     *
     * @param html html
     * @return 过滤后的字符串
     */
    public static String filterHtml(String html) {
        if (html == null) {
            return null;
        }
        return html.replaceAll("<[.[^<]]*>", "");
    }

    /**
     *
     * @param html html
     * @return 过滤后的字符串
     */
    public static String filterHtml2(String html) {
        if (html == null) {
            return null;
        }
        return HtmlUtil.filter(html);
    }

//    public static String md2html(String html) {
//        if (html == null) {
//            return null;
//        }
//
//        return HtmlUtil.htmlToText(html);
//    }


}

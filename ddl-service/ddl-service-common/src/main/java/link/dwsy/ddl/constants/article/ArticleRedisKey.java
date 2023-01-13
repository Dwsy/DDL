package link.dwsy.ddl.constants.article;

/**
 * @Author Dwsy
 * @Date 2022/11/14
 */

public class ArticleRedisKey {

    public static final String ArticleHistoryVersionTitleKey = "article:history:version:id:title:";
    public static final String ArticleHistoryVersionFieldKey = "article:history:version:id:field:";

    public static final String ArticleHistoryVersionContentKey = "article:history:version:id:content:";

    public static final String ArticleHistoryVersionCreateDateKey = "article:history:version:id:date:";
    public static String getHistoryVersionTitleKey(Long id) {
        return ArticleHistoryVersionTitleKey + id;
    }
    public static String getHistoryVersionFieldKey(Long id) {
        return ArticleHistoryVersionFieldKey + id;
    }

    public static String getHistoryVersionContentKey(Long id) {
        return ArticleHistoryVersionContentKey + id;
    }
    public static String getHistoryVersionCreateDateKey(Long id) {
        return ArticleHistoryVersionCreateDateKey + id;
    }
}

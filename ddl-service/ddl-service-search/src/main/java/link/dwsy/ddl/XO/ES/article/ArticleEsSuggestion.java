package link.dwsy.ddl.XO.ES.article;

import link.dwsy.ddl.entity.Article.ArticleTag;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/9/9
 */
public class ArticleEsSuggestion {
    public static List<String> create(String title,String group, List<ArticleTag> tagList) {
        List<String> suggestList = new ArrayList<>();
        tagList.forEach(t->suggestList.add(t.getName()));
        suggestList.add(title);
        suggestList.add(group);
        return suggestList;
    }
}

package link.dwsy.ddl.XO.ES.Question;

import link.dwsy.ddl.entity.QA.QaTag;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/9/9
 */
public class QuestionEsSuggestion {
    public static List<String> create(String title,String group, List<QaTag> tagList) {
        List<String> suggestList = new ArrayList<>();
        tagList.forEach(t->suggestList.add(t.getName()));
        suggestList.add(title);
        suggestList.add(group);
        return suggestList;
    }
}

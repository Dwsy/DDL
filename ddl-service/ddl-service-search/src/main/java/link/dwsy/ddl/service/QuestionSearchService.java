package link.dwsy.ddl.service;

import link.dwsy.ddl.XO.ES.Question.QuestionEsDoc;
import link.dwsy.ddl.util.PageData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @Author Dwsy
 * @Date 2022/12/12
 */

public interface QuestionSearchService {
    @NotNull PageData<QuestionEsDoc> getSearchPageData(int page, int size, String query) throws IOException;
}

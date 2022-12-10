package link.dwsy.ddl.service;

import link.dwsy.ddl.XO.ES.article.ArticleEsDoc;
import link.dwsy.ddl.util.PageData;

import java.io.IOException;

/**
 * @Author Dwsy
 * @Date 2022/12/12
 */

public interface ArticleSearchService {
    PageData<ArticleEsDoc> getSearchPageData(int page, int size, String query) throws IOException;
}

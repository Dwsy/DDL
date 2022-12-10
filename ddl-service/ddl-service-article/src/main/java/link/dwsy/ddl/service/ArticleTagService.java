package link.dwsy.ddl.service;

import link.dwsy.ddl.XO.RB.ArticleTagRB;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.entity.Article.ArticleTag;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ArticleTagService {

    List<ArticleTag> getTagList(Sort sort);

    PageData<fieldVO> getArticleListById(Long id, PageRequest pageRequest);

    List<ArticleTag> getTagListByGroupId(Long id, Sort sort);

    boolean addTag(ArticleTagRB articleTagRB);

    void updateTag(Long id, ArticleTagRB articleTagRB);

    boolean deleteTag(Long id);
}

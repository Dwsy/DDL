package link.dwsy.ddl.service;


import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.VO.UserActionVO;
import link.dwsy.ddl.XO.VO.VersionData;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;
import java.util.Map;

public interface ArticleContentService {


    PageData<fieldVO> getPageList(PageRequest pageRequest, ArticleState articleState);

    PageData<fieldVO> getPageList(PageRequest pageRequest, ArticleState articleState, long articleTagId);

    PageData<fieldVO> getArticleListByUserId(PageRequest pageRequest, ArticleState articleState, long userId);

    ArticleField getArticleById(long id, ArticleState articleState);

    ArticleField getArticleById(long id, Collection<ArticleState> articleStates);

    ArticleField getArticleFieldByIdAndVersion(Long id, Integer version);

    String getArticleContentByIdAndVersion(Long id, Integer version);

    UserActionVO getUserAction(long id);

    String getContent(long id, int type);

    Map<String, VersionData> getHistoryVersionTitle(long articleId);
}

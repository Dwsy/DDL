package link.dwsy.ddl.XO.VO;

import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.entity.Article.ArticleGroup;
import link.dwsy.ddl.entity.Article.ArticleTag;
import link.dwsy.ddl.entity.User.User;

import java.util.Set;

/**
 * @Author Dwsy
 * @Date 2022/8/26
 */
public interface fieldVO {

    String getId();

    User getUser();

    String getTitle();

    String getSummary();

    ArticleState getArticleState();

    boolean isAllowComment();

    long getViewNum();

    long getCollectNum();

    String getBanner();

//    long getArticleContentId();
//    private ArticleContent articleContent;

    Set<ArticleTag> getArticleTags();

    ArticleGroup getArticleGroup();

//    List<ArticleComment> getArticleComments();

}

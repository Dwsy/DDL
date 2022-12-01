package link.dwsy.ddl.repository.Article;

import link.dwsy.ddl.XO.Enum.Article.ArticleState;

import java.util.Date;

/**
 * A Projection for the {@link link.dwsy.ddl.entity.Article.ArticleComment} entity
 */
public interface ArticleCommentInfo {
    ArticleFieldInfo getArticleField();

    /**
     * A Projection for the {@link link.dwsy.ddl.entity.Article.ArticleField} entity
     */
    interface ArticleFieldInfo {
        long getId();

        boolean getDeleted();

        Date getCreateTime();

        Date getLastModifiedTime();

        String getTitle();

        String getSummary();

        ArticleState getArticleState();

        boolean isAllowComment();

        int getViewNum();

        int getCollectNum();

        int getUpNum();

        int getDownNum();

        String getBanner();
    }
}
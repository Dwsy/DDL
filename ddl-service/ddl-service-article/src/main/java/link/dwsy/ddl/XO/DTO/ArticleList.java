package link.dwsy.ddl.XO.DTO;

import link.dwsy.ddl.XO.Enum.ArticleState;
import link.dwsy.ddl.entity.ArticleGroup;
import link.dwsy.ddl.entity.ArticleTag;
import lombok.*;

import java.util.Set;

/**
 * @Author Dwsy
 * @Date 2022/8/25
 */

public interface ArticleList {
//    String getTitle();

    String getTextHtml();

    String getSummary();

    ArticleState getArticleState();

    boolean isAllowComment();

    long getViewNum();

    long getCollectNum();

    String getBanner();

    Set<ArticleTag> getArticleTags();


    ArticleGroup getArticleGroup();

}

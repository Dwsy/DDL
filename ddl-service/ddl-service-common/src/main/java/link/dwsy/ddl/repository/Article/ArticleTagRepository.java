package link.dwsy.ddl.repository.Article;

import link.dwsy.ddl.entity.Article.ArticleTag;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleTagRepository extends JpaRepository<ArticleTag, Long> {
    @Query(value = "select count(article_tag_id) from article_tag_ref where article_content_id =  ?1 ",nativeQuery = true)
    int getCountByArticleId(long id);

    @Query(value = "select article_field_id from article_tag_ref as ref where article_tag_id=?1", nativeQuery = true)
    long[] findArticleContentIdListById(long id);

    List<ArticleTag> findAllByDeletedIsFalse(Sort sort);

//    @Query()
//    void getFieldListByTagId();
}

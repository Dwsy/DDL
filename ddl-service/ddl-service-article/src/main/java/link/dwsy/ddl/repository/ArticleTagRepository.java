package link.dwsy.ddl.repository;

import link.dwsy.ddl.entity.ArticleTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleTagRepository extends JpaRepository<ArticleTag, Long> {
    @Query(value = "select count(article_tag_id) from article_tag_ref where article_content_id =  ?1 ",nativeQuery = true)
    int getCountByArticleId(long id);

    @Query(value = "select article_field_id from article_tag_ref as ref where article_tag_id=?1", nativeQuery = true)
    long[] findArticleContentIdListById(long id);

    List<ArticleTag> findAllByDeletedIsFalse();

//    @Query()
//    void getFieldListByTagId();
}

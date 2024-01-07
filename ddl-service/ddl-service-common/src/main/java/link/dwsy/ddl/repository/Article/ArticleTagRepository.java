package link.dwsy.ddl.repository.Article;

import link.dwsy.ddl.entity.Article.ArticleTag;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ArticleTagRepository extends JpaRepository<ArticleTag, Long> {
    @Query(value = "select count(article_tag_id) from article_tag_ref where article_content_id =  ?1 ",nativeQuery = true)
    int getCountByArticleId(long id);

    @Query(value = "select article_field_id from article_tag_ref as ref where article_tag_id=?1", nativeQuery = true)
    Collection<Long> findArticleContentIdListById(long id);

    List<ArticleTag> findAllByDeletedIsFalse(Sort sort);

    List<ArticleTag> findAllByDeletedIsFalse();

    List<ArticleTag> findByDeletedFalseAndArticleGroupId(long id, Sort sort);

    boolean existsByName(String name);

    @Query(value = "update article_tag set deleted=true where id=?1",nativeQuery = true)
    @Modifying
    int logicallyDeleteById(Long id);

    List<ArticleTag> findByDeletedFalseAndArticleGroup_IdNotNullAndIndexPageDisplayIsTrue(Sort sort);
//    @Query()
//    void getFieldListByTagId();

//    testVo findTopById(long id);







}

package link.dwsy.ddl.XO.CustomER.repository;

import link.dwsy.ddl.XO.CustomER.entity.ArticleContentCustom;
import link.dwsy.ddl.XO.CustomER.entity.ArticleTagCustom;
import link.dwsy.ddl.entity.ArticleTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ArticleTagRepositoryCustom extends JpaRepository<ArticleTagCustom, Long> {
    @Query(value = "select count(article_tag_id) from article_tag_ref where article_content_id =  ?1 ",nativeQuery = true)
    int getCountByArticleId(long id);

    @Query(value = "select article_content_id from article_tag_ref where article_tag_id=?1", nativeQuery = true)
    Long[] findArticleContentIdListById(long id);

}

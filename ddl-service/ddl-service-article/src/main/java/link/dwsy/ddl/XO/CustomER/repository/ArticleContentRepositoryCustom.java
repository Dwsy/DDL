package link.dwsy.ddl.XO.CustomER.repository;

import link.dwsy.ddl.XO.CustomER.entity.ArticleContentCustom;
import link.dwsy.ddl.XO.DTO.ArticleList;
import link.dwsy.ddl.XO.DTO.OnlyTest;
import link.dwsy.ddl.XO.Enum.ArticleState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

//public interface ArticleContentRepository extends JpaRepository<ArticleContent, Long> {
public interface ArticleContentRepositoryCustom  extends JpaRepository<ArticleContentCustom, Long>{

    //    Page<ArticleContent> findAllByDeletedIsFalseOrDeletedIsNull(Pageable pageable);
    Page<ArticleContentCustom> findAllByDeletedIsFalseAndArticleState(ArticleState articleState, Pageable pageable);


    @Query(nativeQuery = true, value = "select id, create_time, deleted, last_modified_time" +
            ", allow_comment, article_state, banner, collect_num, text_html" +
            " title, view_num from article_content as act  where act.id in ?1")
    List<ArticleContentCustom> findAllByIdIn(List<Long> longs);

    ArticleContentCustom findByIdAndArticleState(long id, ArticleState articleState);


    //    ArticleContent findByTitleIsNotNull();
//    @EntityGraph(attributePaths = "articleTags")
    @Query("select acc.title, acc.articleComments from ArticleContent acc left outer join  acc.articleComments comment" +
            " on acc.id=comment.articleContent.id where acc.id =?1")
    ArticleContentCustom findByVbyId(Long id);

    OnlyTest findByIdAndAndDeletedIsFalse(long id);
//    @Query("select organization.id, organization.name, organization.metas from
//    Organization organization left join fetch organization.metas where organization.id = ?1")
//    List<OrganizationView> findSubsetById(Long id);

//
}

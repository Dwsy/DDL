package link.dwsy.ddl.XO.CustomER.repository;

import link.dwsy.ddl.XO.CustomER.entity.ArticleContentCustom;
import link.dwsy.ddl.XO.Enum.ArticleState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

//public interface ArticleContentRepository extends JpaRepository<ArticleContent, Long> {
public interface ArticleContentRepositoryCustom extends JpaRepository<ArticleContentCustom, Long> {

//    Page<ArticleContent> findAllByDeletedIsFalseOrDeletedIsNull(Pageable pageable);
    Page<ArticleContentCustom> findAllByDeletedIsFalseAndArticleState(ArticleState articleState, Pageable pageable);

    ArticleContentCustom findByIdAndArticleState(long id, ArticleState articleState);


    //    ArticleContent findByTitleIsNotNull();
//    @EntityGraph(attributePaths = "articleTags")
    ArticleContentCustom findByTitleIsNotNull();


//
}

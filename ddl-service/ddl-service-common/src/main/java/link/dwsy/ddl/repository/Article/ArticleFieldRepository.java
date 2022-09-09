package link.dwsy.ddl.repository.Article;

import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.entity.Article.ArticleField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ArticleFieldRepository extends JpaRepository<ArticleField, Long> {
    Page<fieldVO> findAllByDeletedIsFalseAndArticleState(ArticleState articleState, Pageable pageable);

    Page<fieldVO> findAllByIdInAndDeletedIsFalseAndArticleState(long[] tid, ArticleState articleState, Pageable pageable);

    Page<fieldVO> findAllByDeletedIsFalseAndArticleGroupIdAndArticleState(long gid, ArticleState articleState, Pageable pageable);

    ArticleField findByIdAndDeletedIsFalseAndArticleState(long id, ArticleState articleState);

    Optional<ArticleField> findByIdAndDeletedFalseAndArticleState(long id, ArticleState articleState);


//    Optional<ArticleField> findByIdAndDeletedIsFalseAndArticleState(long id, ArticleState articleState);
    @Query(value = "select af from ArticleField af where af.deleted=false and af.articleState=?1")
    List<fieldVO> findBySqlTest(ArticleState articleState, Pageable pageable);

    boolean existsByDeletedFalseAndIdAndUser_Id(long aid, long uid);

    boolean existsByDeletedIsFalseAndIdInAndUser_Id(Collection<Long> ids, long id);
    int countByDeletedIsFalseAndIdInAndUser_Id(Collection<Long> ids, long id);
//    @Query(nativeQuery = true,value = "select * from article_field where deleted is false and user_id=?1")

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update article_field set deleted=true where id=?1")
    int logicallyDeleted(long aid);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update article_field set deleted=false where id=?1")
    int logicallyRecovery(long aid);

    boolean existsByDeletedFalseAndAllowCommentTrueAndId(long id);

    @Query(nativeQuery = true,
    value = "select * from article_field where deleted is false and article_state=1")
    Long[] findAllId();

//    ArticleField findArticleFieldsByDeletedIsFalse(Long articleId);
}

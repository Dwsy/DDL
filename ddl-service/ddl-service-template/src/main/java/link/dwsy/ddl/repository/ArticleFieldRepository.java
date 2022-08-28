package link.dwsy.ddl.repository;

import link.dwsy.ddl.XO.Enum.ArticleState;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.entity.ArticleField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleFieldRepository extends JpaRepository<ArticleField, Long> {
    Page<fieldVO> findAllByDeletedIsFalseAndArticleState(ArticleState articleState, Pageable pageable);

    Page<fieldVO> findAllByIdInAndDeletedIsFalseAndArticleState(long[] tid, ArticleState articleState, Pageable pageable);

    Page<fieldVO> findAllByDeletedIsFalseAndArticleGroupIdAndArticleState(long gid, ArticleState articleState, Pageable pageable);
    ArticleField findByIdAndDeletedIsFalseAndArticleState(long id,ArticleState articleState);

    @Query(value = "select af from ArticleField af where af.deleted=false and af.articleState=?1")
    List<fieldVO> findBySqlTest(ArticleState articleState, Pageable pageable);

}

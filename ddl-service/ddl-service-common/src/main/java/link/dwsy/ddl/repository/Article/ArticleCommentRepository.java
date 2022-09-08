package link.dwsy.ddl.repository.Article;

import link.dwsy.ddl.entity.Article.ArticleComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
    //    @Query(nativeQuery = true,"select count(id) from article_comment where article_content_id = :1")
    @Query("select count(articleField) from ArticleComment where articleField.id = ?1 and deleted is false ")
    int getCountByArticleId(long id);

    Page<ArticleComment> findAllByDeletedIsFalseAndArticleFieldIdAndParentCommentId(long fid, long pid, Pageable pageable);

    Set<ArticleComment> findAllByDeletedIsFalseAndArticleFieldIdAndParentCommentId(long fid, long pid);

    @Query(value = "select parent_comment_id=0 from article_comment a where id=?1", nativeQuery = true)
    boolean isFirstAnswer(long parentCommentId);

    boolean existsByDeletedFalseAndIdAndArticleFieldId(long id, long articleFieldId);


    @Query(value = "update article_comment set deleted=true where user_id=?1 and id=?2",nativeQuery = true)
    @Modifying
    int logicallyDelete(Long id, Long aLong);

    @Query(value = "update article_comment set deleted=false where user_id=?1 and id=?2",nativeQuery = true)
    @Modifying
    int logicallyRecovery(Long id, Long aLong);
}

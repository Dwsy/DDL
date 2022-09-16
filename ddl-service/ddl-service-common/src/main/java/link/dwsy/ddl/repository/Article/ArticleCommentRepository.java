package link.dwsy.ddl.repository.Article;

import link.dwsy.ddl.XO.Enum.Article.CommentType;
import link.dwsy.ddl.entity.Article.ArticleComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
    //    @Query(nativeQuery = true,"select count(id) from article_comment where article_content_id = :1")
    @Query("select count(articleField) from ArticleComment where articleField.id = ?1 and deleted is false ")
    int getCountByArticleId(long id);

    Page<ArticleComment> findAllByDeletedIsFalseAndArticleFieldIdAndParentCommentId(long fid, long pid, Pageable pageable);

    Optional<ArticleComment> findByDeletedFalseAndId(long id);

    Set<ArticleComment> findAllByDeletedIsFalseAndArticleFieldIdAndParentCommentId(long fid, long pid);

    @Query(value = "select parent_comment_id=0 from article_comment a where id=?1", nativeQuery = true)
    boolean isFirstAnswer(long parentCommentId);

    boolean existsByDeletedFalseAndIdAndArticleFieldIdAndCommentType(long id, long articleField_id, CommentType commentType);

    boolean existsByDeletedFalseAndUser_IdAndArticleField_IdAndParentCommentIdAndCommentTypeNot(long user_id, long articleField_id, long parentCommentId, CommentType commentType);

    ArticleComment findByDeletedFalseAndUser_IdAndArticleField_IdAndParentCommentIdAndCommentTypeNot(long user_id, long articleField_id, long parentCommentId, CommentType commentType);

    ArticleComment findByDeletedFalseAndIdAndCommentType(long id, CommentType commentType);


    @Query(value = "update article_comment set deleted=true where user_id=?1 and id=?2", nativeQuery = true)
    @Modifying
    int logicallyDelete(Long id, Long aLong);

    @Query(value = "update article_comment set deleted=false where user_id=?1 and id=?2", nativeQuery = true)
    @Modifying
    int logicallyRecovery(Long id, Long aLong);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update article_comment set up_num=up_num+?2 where id=?1")
    int upNumIncrement(long aid, int num);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update article_comment set down_num=down_num+?2 where id=?1")
    int downNumIncrement(long aid, int num);

    @Query(nativeQuery = true, value =
            "select user_id from article_comment where id=?1 and deleted=false")
    Long getUserIdByCommentId(Long id);
}

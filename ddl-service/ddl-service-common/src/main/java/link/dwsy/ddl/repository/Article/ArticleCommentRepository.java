package link.dwsy.ddl.repository.Article;

import link.dwsy.ddl.XO.Enum.Article.CommentType;
import link.dwsy.ddl.entity.Article.ArticleComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
    //    @Query(nativeQuery = true,"select count(id) from article_comment where article_content_id = :1")
    @Query("select count(articleField) from ArticleComment where articleField.id = ?1 and deleted is false ")
    int getCountByArticleId(long id);

    Page<ArticleComment> findAllByDeletedIsFalseAndArticleFieldIdAndParentCommentId(long fid, long pid, Pageable pageable);

    Page<ArticleComment> findByDeletedFalseAndArticleField_IdAndParentCommentIdAndCommentType(long id, long parentCommentId, CommentType commentType, Pageable pageable);



    Optional<ArticleComment> findByDeletedFalseAndId(long id);


    Page<ArticleComment> findByArticleField_IdAndParentCommentIdAndCommentTypeAndDeletedFalse(long id, long parentCommentId, CommentType commentType, Pageable pageable);





//    @Query(value = "select parent_comment_id=0 from article_comment a where id=?1", nativeQuery = true)
//    boolean isFirstAnswer(long parentCommentId);

    @Query(value = "select parent_comment_id=0 from article_comment a where id=?1", nativeQuery = true)
    boolean isFirstComment(long parentCommentId);

    boolean existsByDeletedFalseAndIdAndArticleFieldIdAndCommentType(
            long id, long articleField_id, CommentType commentType);

    boolean existsByDeletedFalseAndUser_IdAndArticleField_IdAndParentCommentIdAndCommentTypeNot
            (long user_id, long articleField_id, long parentCommentId, CommentType commentType);

    ArticleComment findByDeletedFalseAndUser_IdAndArticleField_IdAndParentCommentIdAndCommentTypeNot
            (long user_id, long articleField_id, long parentCommentId, CommentType commentType);

    ArticleComment findByDeletedFalseAndIdAndCommentType(long id, CommentType commentType);


    @Query(value = "update article_comment set deleted=true id=?2", nativeQuery = true)
    @Modifying
    int logicallyDelete(Long commentId);

    @Query(value = "update article_comment set deleted=false where user_id=?1 and id=?2", nativeQuery = true)
    @Modifying
    int logicallyRecovery(Long id, Long aLong);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update article_comment set up_num=up_num+?2 where id=?1")
    int upNumIncrement(long cid, int num);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update article_comment set down_num=down_num+?2 where id=?1")
    int downNumIncrement(long cid, int num);

    @Query(nativeQuery = true, value =
            "select user_id from article_comment where id=?1 and deleted=false")
    Long getUserIdByCommentId(Long id);

    @Transactional
    @Modifying
    @Query("update ArticleComment a set a.commentType = ?1 where a.id = ?2 and a.deleted = false")
    int updateCommentTypeByIdAndDeletedFalse(CommentType commentType, long id);

    Optional<ArticleComment> findByUserIdAndParentCommentIdAndCommentTypeIn
            (long uid, long parentCommentId, Collection<CommentType> commentTypes);

    Page<ArticleComment> findByDeletedFalseAndUser_IdAndParentCommentIdAndCommentType
            (long uid, long parentCommentId, CommentType commentType, Pageable pageable);



    Optional<ArticleComment> findByDeletedFalseAndUser_IdAndParentCommentIdAndCommentTypeInAndArticleField_Id
            (long id, long parentCommentId, Collection<CommentType> commentTypes, long id1);


    @Query(nativeQuery = true,value = "select text from article_comment where id=?1 and deleted=false")
    String getText(Long commentId);

    @Query("select (count(a) > 0) from ArticleComment a " +
            "where a.deleted = false and a.id = ?1 and a.replyUserCommentId = 0")
    boolean notIsSecondaryComment(long id);

    ArticleComment findFirstByDeletedFalseAndArticleField_IdAndParentCommentIdAndCommentTypeOrderByCommentSerialNumberDesc
            (long afId, long parentCommentId, CommentType commentType);

    boolean existsByDeletedFalseAndArticleField_IdAndIdAndCommentType(long articleFieldId, long commentId, CommentType commentType);

    List<ArticleComment> findByParentCommentIdAndDeletedFalseAndCommentTypeIn(long parentCommentId, Collection<CommentType> commentTypes);


    
    

}

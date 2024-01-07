package link.dwsy.ddl.repository.Article;

import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.Projection.ArticleFieldInfo;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.entity.Article.ArticleField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ArticleFieldRepository extends JpaRepository<ArticleField, Long> {
//    @Query(value = "select a from article_field as a where a.deleted = false and a.id in ?1 order by id,(?2) asc ",nativeQuery = true)
//    List<ArticleField> getByDeletedFalseAndIdIn(List<Long> ids, String order);

    List<ArticleField> findByDeletedFalseAndIdIn(List<Long> ids);

//    List<ArticleField> findByDeletedFalseAndIdIn(List<Long> ids, Sort.Order sort);
    Page<fieldVO> findAllByDeletedIsFalseAndArticleState(ArticleState articleState, Pageable pageable);


//    Page<fieldVO> findByUser_IdAndArticleStateAndDeleted(long user_id, ArticleState articleState, boolean deleted, Pageable pageable);
//
//    Page<fieldVO> findByUser_IdAndArticleStateNotAndDeleted(long user_id, ArticleState articleState, boolean deleted, Pageable pageable);
//
//    Page<ArticleField> findByUser_IdAndArticleStateNot(long id, ArticleState articleState, Pageable pageable);


//    Page<fieldVO> findByDeletedFalseAndUser_IdAndArticleState(long id, ArticleState articleState, Pageable pageable);


//    Page<fieldVO> findAllByDeletedIsFalseAndArticleStateIn(ArticleState articleState, Pageable pageable);

    Page<fieldVO> findByDeletedFalseAndArticleStateAndArticleTags_Id(ArticleState articleState, long id, Pageable pageable);


    Page<fieldVO> findAllByIdInAndDeletedIsFalseAndArticleState(Collection<Long> id, ArticleState articleState, Pageable pageable);

    Page<fieldVO> findAllByDeletedIsFalseAndArticleGroupIdAndArticleState(long gid, ArticleState articleState, Pageable pageable);

    ArticleField findByIdAndDeletedIsFalseAndArticleState(long id, ArticleState articleState);

    ArticleField findByIdAndDeletedIsFalseAndArticleStateIn(long id, Collection<ArticleState> articleStates);

    ArticleField findByDeletedFalseAndId(long id);


    @Query(nativeQuery = true, value = "select user_id from article_field where deleted is false and id=?1")
    Long findUserIdById(long id);

    @Query(nativeQuery = true, value = "select user_id from article_field where deleted is false and article_content_id=?1")
    Long findUserIdByContentId(long id);


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
    void logicallyDeleted(long aid);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update article_field set deleted=false where id=?1")
    void logicallyRecovery(long aid);

    boolean existsByDeletedFalseAndAllowCommentTrueAndId(long id);

    @Query(nativeQuery = true,
            value = "select * from article_field where deleted is false and article_state=1")
    Long[] findAllId();

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update article_field set comment_num=comment_num+?2 where id=?1")
    void commentNumIncrement(long aid, int num);


    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update article_field set up_num=up_num+?2 where id=?1")
    void upNumIncrement(long aid, int num);


    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update article_field set down_num=down_num+?2 where id=?1")
    void downNumIncrement(long aid, int num);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update article_field set view_num=view_num+?2 where id=?1")
    void viewNumIncrement(long aid, int num);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update article_field set collect_num=collect_num+?2 where id=?1")
    void collectNumIncrement(long aid, int num);

    @Query("select a from ArticleField a where a.deleted = false and a.id = ?1")
    Optional<ArticleFieldInfo> getTitle(long id);

    @Query(nativeQuery = true, value =
            "select user_id from article_field where id=?1")
    Long getUserIdByArticleId(Long id);

    Page<fieldVO> findByDeletedFalseAndArticleStateAndUser_Id(ArticleState articleState, long uid, Pageable pageable);


    @Query(nativeQuery = true, value =
            "select article_content_id from article_field where id = ?1")
    BigInteger getContentIdById(long fid);

    Page<fieldVO> findByUser_IdAndArticleStateAndDeleted(long id, ArticleState articleState, boolean deleted, Pageable pageable);

    Page<fieldVO> findByUser_IdAndArticleStateAndArticleTags_IdAndDeleted(long id, ArticleState articleState, long tagId, boolean deleted, Pageable pageable);

    Page<fieldVO> findByUser_IdAndArticleStateNotAndDeleted(long id, ArticleState articleState, boolean deleted, Pageable pageable);

    Page<fieldVO> findByDeletedFalseAndArticleStateNot(ArticleState articleState, Pageable pageable);
    Page<fieldVO> findByDeletedFalseAndArticleState(ArticleState articleState, Pageable pageable);



    Page<fieldVO> findByUser_IdAndArticleStateNotAndArticleTags_IdAndDeleted(long id, ArticleState articleState, long tagId, boolean deleted, Pageable pageable);

    int countByDeletedFalseAndUser_IdAndArticleState(long id, ArticleState articleState);

    int countByDeletedFalseAndUser_IdAndArticleStateNot(long id, ArticleState articleState);

    int countByDeletedFalseAndArticleState(ArticleState articleState);
    int countByDeletedFalseAndArticleStateNot(ArticleState articleState);




//    ArticleField findArticleFieldsByDeletedIsFalse(Long articleId);


    @Query(nativeQuery = true, value = "select count(*) from users where deleted is true and id=(select user_id from article_field  where deleted is false and id=?1) and  deleted = true")
    int userIsCancellation(long userId);

    @Query(nativeQuery = true,value = "select article_tag_id from article_tag_ref where article_field_id=?1")
    List<Long> getTagIdsById(long parseLong);

    @Query(nativeQuery = true, value = "select article_group_id from article_field where id=?1")
    long getGroupIdById(long parseLong);


}

package link.dwsy.ddl.repository.Infinity;

import link.dwsy.ddl.XO.Enum.InfinityType;
import link.dwsy.ddl.XO.Projection.InfinityInfo;
import link.dwsy.ddl.entity.Infinity.Infinity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/11/23
 */

public interface InfinityRepository extends JpaRepository<Infinity, Long> {
    List<Infinity> findByDeletedFalseAndIdIn(Collection<Long> ids);
    Infinity findByDeletedFalseAndId(long id);


    Infinity findByDeletedFalseAndUser_IdAndIdAndType(long user_id, long id, InfinityType type);


    Page<Infinity> findByDeletedFalseAndInfinityTopics_IdIn(Collection<Long> ids, Pageable pageable);

    Page<Infinity> findByDeletedFalseAndInfinityTopics_IdInAndType(Collection<Long> ids, InfinityType type, Pageable pageable);


    Page<Infinity> findByDeletedFalseAndInfinityClub_IdAndType(long id, InfinityType type, Pageable pageable);
    Page<Infinity> findByDeletedFalseAndInfinityClub_IdAndTypeIn(long id, Collection<InfinityType> types, Pageable pageable);

    Page<Infinity> findByDeletedFalseAndUser_IdAndTypeIn(long id, Collection<InfinityType> types, Pageable pageable);
    Page<Infinity> findByDeletedFalseAndUser_IdAndTypeNot(long id, InfinityType type, Pageable pageable);




    Page<Infinity> findByDeletedFalseAndType(InfinityType type, Pageable pageable);
    Page<Infinity> findByDeletedFalseAndTypeIn(Collection<InfinityType> type, Pageable pageable);


    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update infinity set up_num=up_num+?2 where id=?1")
    void upNumIncrement(long id, int num);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update infinity set view_num=view_num+?2 where id=?1")
    void viewNumIncrement(long id, int num);


    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update infinity set collect_num=collect_num+?2 where id=?1")
    void collectNumIncrement(long id, int num);

    boolean existsByDeletedFalseAndIdAndType(long id, InfinityType type);

    boolean existsByDeletedFalseAndIdAndTypeIn(long id, Collection<InfinityType> types);

    boolean existsByDeletedFalseAndIdAndTypeNot(long id, InfinityType type);

    Page<Infinity> findByDeletedFalseAndParentTweetIdAndType(Long parentTweetId, InfinityType type, Pageable pageable);

    Page<Infinity> findByDeletedFalseAndParentTweetIdAndTypeAndReplyUserTweetId(Long parentTweetId, InfinityType type, Long replyUserTweetId, Pageable pageable);

    Page<Infinity> findByDeletedFalseAndTypeAndReplyUserTweetId(InfinityType type, Long replyUserTweetId, Pageable pageable);

    boolean existsByDeletedFalseAndUser_IdAndIdAndType(long userId, long id, InfinityType type);

    boolean existsByDeletedFalseAndUser_IdAndParentTweetIdAndType(long id, Long parentTweetId, InfinityType type);


    Infinity findByDeletedFalseAndUser_IdAndParentTweetIdAndType(long userId, Long parentTweetId, InfinityType type);

    @Query("select i.user.id from Infinity i where i.id = ?1")
    Long getUserIdById(long id);

    @Query("select i.content from Infinity i where i.id = ?1")
    String getContentById(long id);


    @Query("select i.infinityClub,i.infinityTopics from Infinity i where i.id = ?1")
    InfinityInfo TopicIdsAndClubId(long id);


}

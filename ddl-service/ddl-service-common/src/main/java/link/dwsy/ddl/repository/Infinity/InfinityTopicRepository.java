package link.dwsy.ddl.repository.Infinity;

import link.dwsy.ddl.entity.Infinity.InfinityTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author Dwsy
 * @Date 2022/11/23
 */

public interface InfinityTopicRepository extends JpaRepository<InfinityTopic, Long> {
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "insert into infinity_topic_follow_ref (topic_id, user_id,create_time) values (?1, ?2,now())")
    int followTopic(long topicId, long userId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from infinity_topic_follow_ref where topic_id = ?1 and user_id = ?2 ")
    int unFollowTopic(long topicId, long userId);

    @Query(nativeQuery = true,
            value = "select count(*) from infinity_topic_follow_ref where user_id = ?2 and topic_id = ?1")
    int isFollow(long topicId, long userId);


    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update infinity_topic set view_num=view_num+?2 where id=?1")
    void viewNumIncrement(long id, int num);


    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update infinity_topic set follower_num=follower_num+?2 where id=?1")
    void followerNumIncrement(long id, int num);

    boolean existsByDeletedFalseAndName(String name);

    boolean existsByDeletedAndId(boolean deleted, long id);


}

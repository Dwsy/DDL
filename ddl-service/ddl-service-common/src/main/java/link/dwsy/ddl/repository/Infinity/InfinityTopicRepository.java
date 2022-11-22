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
    @Query(nativeQuery = true, value = "insert into infinity_club_ref (club_id, user_id,delete,create_time) values (?1, ?,false,now())")
    void followTopic(long clubId, long userId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "insert into infinity_topic_ref (club_id, user_id,delete,create_time) values (?1, ?,false,now())")
    void unFollowTopic(long clubId, long userId);

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
}

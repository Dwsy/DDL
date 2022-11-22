package link.dwsy.ddl.repository.Infinity;

import link.dwsy.ddl.entity.Infinity.InfinityClub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author Dwsy
 * @Date 2022/11/23
 */

public interface InfinityClubRepository extends JpaRepository<InfinityClub, Long> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "insert into infinity_club_ref (club_id, user_id,delete,create_time) values (?1, ?,false,now())")
    void followClub(long clubId, long userId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "insert into infinity_club_ref (club_id, user_id,delete,create_time) values (?1, ?,false,now())")
    void unFollowClub(long clubId, long userId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update infinity_club set view_num=view_num+?2 where id=?1")
    void viewNumIncrement(long id, int num);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "update infinity_club set follower_num=follower_num+?2 where id=?1")
    void followerNumIncrement(long id, int num);


}

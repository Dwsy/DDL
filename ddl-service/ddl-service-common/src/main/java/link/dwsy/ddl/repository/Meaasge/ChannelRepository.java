package link.dwsy.ddl.repository.Meaasge;

import link.dwsy.ddl.entity.Message.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/9/2
 */

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "insert into channel_user_ref (channel_id, user_id) values (?1,?2)")
    int addUser(long cid, long uid);


    @Query(nativeQuery = true,
            value = "select count(*) from channel_user_ref where channel_id = ?1 and user_id = ?2")
    int existsUser(long cid, long uid);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "delete from channel_user_ref where channel_id = ?1 and user_id = ?2")
    int exitChannel(long cid, long uid);

    @Query(nativeQuery = true,
            value = "select channel_id from channel_user_ref where user_id=?1")
    List<Long> findUserChannel(long uid);

    @Query(nativeQuery = true,
            value = "select user_id from channel_user_ref where channel_id=?1")
    List<Long> findChannelUser(long l);
//
//    @Query(nativeQuery = true,value = ""
}

package link.dwsy.ddl.repository.User;

import link.dwsy.ddl.entity.User.UserTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
public interface UserTagRepository extends JpaRepository<UserTag, Long> {
    List<UserTag> findByDeletedFalseAndIdIn(Collection<Long> tagIds);

    @Query(nativeQuery = true,
            value = "select user_id from user_tag_ref where  user_tag_id = ?1")
    ArrayList<Long> finduserByTagId(long tagId);

    @Query(nativeQuery = true,
            value = "select user_id from user_tag_ref where  user_tag_id in (?1) order by points desc")
    ArrayList<Long> findUserByTagIdIn(Collection<Long> tagIds);

}

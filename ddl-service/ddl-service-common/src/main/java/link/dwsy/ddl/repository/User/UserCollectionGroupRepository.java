package link.dwsy.ddl.repository.User;

import link.dwsy.ddl.entity.User.UserCollectionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @Author Dwsy
 * @Date 2022/9/9
 */

public interface UserCollectionGroupRepository extends JpaRepository<UserCollectionGroup, Long> {
    boolean existsByUserIdAndGroupName(Long userId, String groupName);

    UserCollectionGroup findByIdAndUserIdAndDeletedIsFalse(long gid, long uid);

    List<UserCollectionGroup> findByDeletedFalseAndUserId(Long userId);

    Optional<UserCollectionGroup> findByDeletedFalseAndUserIdAndId(Long userId, long id);


}

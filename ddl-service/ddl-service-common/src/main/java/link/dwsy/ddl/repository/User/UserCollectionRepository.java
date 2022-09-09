package link.dwsy.ddl.repository.User;

import link.dwsy.ddl.XO.Enum.CollectionType;
import link.dwsy.ddl.entity.User.UserCollection;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author Dwsy
 * @Date 2022/9/9
 */

public interface UserCollectionRepository extends JpaRepository<UserCollection, Long> {
    boolean existsByUserIdAndSourceIdAndCollectionTypeAndDeletedFalse(Long userId, Long sourceId, CollectionType collectionType);

}

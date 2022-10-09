package link.dwsy.ddl.repository.User;

import link.dwsy.ddl.XO.Enum.User.CollectionType;
import link.dwsy.ddl.entity.User.UserCollection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @Author Dwsy
 * @Date 2022/9/9
 */

public interface UserCollectionRepository extends JpaRepository<UserCollection, Long> {
    boolean existsByUserIdAndSourceIdAndCollectionTypeAndDeletedFalse(Long userId, Long sourceId, CollectionType collectionType);

    boolean existsByDeletedFalseAndUserIdAndSourceIdAndUserCollectionGroup_IdAndCollectionType(Long userId, Long sourceId, long userCollectionGroup_id, CollectionType collectionType);

    Optional<UserCollection> findByDeletedFalseAndUserIdAndSourceIdAndUserCollectionGroup_IdAndCollectionType(Long userId, Long sourceId, long userCollectionGroup_id, CollectionType collectionType);
    Optional<UserCollection> findByUserIdAndSourceIdAndUserCollectionGroup_IdAndCollectionType(Long userId, Long sourceId, long userCollectionGroup_id, CollectionType collectionType);


    List<UserCollection> findByDeletedFalseAndUserIdAndSourceIdAndCollectionType(Long uid, Long sourceId, CollectionType collectionType);
}

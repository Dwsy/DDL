package link.dwsy.ddl.repository.User;

import link.dwsy.ddl.XO.VO.LevelAndExperienceVO;
import link.dwsy.ddl.entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
public interface UserRepository extends JpaRepository<User,Long> {

    @Query(nativeQuery = true, value = "select level from users where id = ?1 and deleted is false")
    int getUserLevelById(Long id);

    @Query(nativeQuery = true, value = "select level,experience from users where id = ?1 and deleted is false")
    LevelAndExperienceVO getUserLevelAndExperience(long id);
    User findUserByUsernameAndPasswordAndDeletedIsFalse(String Username, String Password);
    User findUserByPhoneAndPasswordAndDeletedIsFalse(String Username, String Password);

    User findUserByIdAndDeletedIsFalse(Long id);
    User findUserById(Long id);

    List<User> findByIdInAndDeletedFalse(Collection<Long> ids);
//    List<User> findAllByIdAndDeletedIsFalse(List<Long> id);

    User findUserByUsernameAndDeletedIsFalse(String username);

    @Query(value = "select nickname from users where id=?1",nativeQuery = true)
    String getUserNicknameById(long uid);

    boolean existsByDeletedFalseAndEmail(String email);

    List<User> findByDeletedFalse();

    boolean existsByDeletedFalseAndId(long id);

    boolean existsByDeletedTrueAndId(long id);

    @Query(value = "select experience from users where deleted is false",nativeQuery = true)
    int getUserExpById(Long id);

    @Query(value = "update users set level=?2 where deleted is false and id=?1",nativeQuery = true)
    @Modifying
    @Transactional
    void updateUserLevel(Long id, int level);
}

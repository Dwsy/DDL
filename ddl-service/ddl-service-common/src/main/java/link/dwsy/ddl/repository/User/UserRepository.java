package link.dwsy.ddl.repository.User;

import link.dwsy.ddl.entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
public interface UserRepository extends JpaRepository<User,Long> {

    @Query(nativeQuery = true, value = "select level from users where id = ?1 and deleted is false")
    int getUserLevelById(Long id);

    User findUserByUsernameAndPasswordAndDeletedIsFalse(String Username, String Password);
    User findUserByPhoneAndPasswordAndDeletedIsFalse(String Username, String Password);

    User findUserByIdAndDeletedIsFalse(Long id);

    List<User> findByIdInAndDeletedFalse(Collection<Long> ids);
//    List<User> findAllByIdAndDeletedIsFalse(List<Long> id);

    User findUserByUsernameAndDeletedIsFalse(String username);

    @Query(value = "select nickname from users where id=?1",nativeQuery = true)
    String findUserNicknameById(long uid);

}

package link.dwsy.ddl.repository;

import link.dwsy.ddl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
public interface UserRepository extends JpaRepository<User,Long> {

    User findUserByUsernameAndPasswordAndDeletedIsFalse(String Username, String Password);

    User findByDeletedFalseAndUsername(String username);
    User findByDeletedFalseAndPhone(String phone);


    User findUserByPhoneAndPasswordAndDeletedIsFalse(String Username, String Password);

    Optional<User> findByDeletedFalseAndId(long id);


    User findUserByUsernameAndDeletedIsFalse(String username);
}

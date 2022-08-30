package link.dwsy.ddl.repository;

import link.dwsy.ddl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
public interface UserRepository extends JpaRepository<User,Long> {

    User findUserByUsernameAndPasswordAndDeletedIsFalse(String Username, String Password);
    User findUserByPhoneAndPasswordAndDeletedIsFalse(String Username, String Password);


    User findUserByUsernameAndDeletedIsFalse(String username);
}

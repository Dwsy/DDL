package link.dwsy.ddl.repository.User;

import link.dwsy.ddl.entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
public interface UserRepository extends JpaRepository<User,Long> {

    User findUserByUsernameAndPasswordAndDeletedIsFalse(String Username, String Password);
    User findUserByPhoneAndPasswordAndDeletedIsFalse(String Username, String Password);

    User findUserByIdAndDeletedIsFalse(Long id);

    User findUserByUsernameAndDeletedIsFalse(String username);
}

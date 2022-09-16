package link.dwsy.ddl.demo.repository;

import link.dwsy.ddl.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
public interface UserRepository extends JpaRepository<User,Long> {

    List<User> findUserByUsernameAndPasswordAndDeletedIsFalse(String Username, String Password);
}

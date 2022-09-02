package link.dwsy.ddl.repository.User;

import link.dwsy.ddl.entity.User.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author Dwsy
 * @Date 2022/9/2
 */

public interface SystemUserRepository extends JpaRepository<SystemUser,Long> {
}

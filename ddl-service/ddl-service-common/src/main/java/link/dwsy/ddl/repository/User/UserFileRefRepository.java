package link.dwsy.ddl.repository.User;

import link.dwsy.ddl.entity.User.UserFileRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author Dwsy
 * @Date 2022/12/22
 */

@Repository
public interface UserFileRefRepository extends JpaRepository<UserFileRef, Long> {
}

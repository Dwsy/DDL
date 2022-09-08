package link.dwsy.ddl.repository.User;

import link.dwsy.ddl.XO.Enum.UserActiveType;
import link.dwsy.ddl.entity.User.UserActive;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

/**
 * @Author Dwsy
 * @Date 2022/9/5
 */

public interface UserActiveRepository extends JpaRepository<UserActive, Long> {
    Optional<UserActive> findByUserActiveTypeAndCreateTimeGreaterThanEqualAndDeletedFalse(UserActiveType userActiveType, Date createTime);



}

package link.dwsy.ddl.repository.User;

import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.entity.User.UserActive;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * @Author Dwsy
 * @Date 2022/9/5
 */

public interface UserActiveRepository extends JpaRepository<UserActive, Long> {



    boolean existsByCreateTimeBetween(Date createTimeStart, Date createTimeEnd);


    boolean existsByUserIdIsAndUserActiveTypeAndCreateTimeBetween(Long id, UserActiveType userActiveType, Date createTimeStart, Date createTimeEnd);



}

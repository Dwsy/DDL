package link.dwsy.ddl.repository.User;

import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.entity.User.UserActive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Date;

/**
 * @Author Dwsy
 * @Date 2022/9/5
 */

public interface UserActiveRepository extends JpaRepository<UserActive, Long> {
    UserActive findByDeletedFalseAndUserIdAndUserActiveTypeAndSourceId(Long userId, UserActiveType userActiveType, Long sourceId);
    Page<UserActive> findByDeletedFalseAndUserIdAndUserActiveType(Long userId, UserActiveType userActiveType, Pageable pageable);



    boolean existsByCreateTimeBetween(Date createTimeStart, Date createTimeEnd);


    boolean existsByUserIdIsAndUserActiveTypeAndCreateTimeBetween
            (Long id, UserActiveType userActiveType, Date createTimeStart, Date createTimeEnd);

    Page<UserActive> findByDeletedFalseAndUserIdAndUserActiveTypeIn(Long userId, Collection<UserActiveType> userActiveTypes, Pageable pageable);





}

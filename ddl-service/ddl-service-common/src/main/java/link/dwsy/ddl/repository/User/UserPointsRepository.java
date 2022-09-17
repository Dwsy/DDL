package link.dwsy.ddl.repository.User;

import link.dwsy.ddl.XO.Enum.User.PointsType;
import link.dwsy.ddl.entity.User.UserPoints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface UserPointsRepository extends JpaRepository<UserPoints, Long> {
    List<UserPoints> findByUserIdAndPointsTypeAndCreateTimeBetween(Long userId, PointsType pointsType, Date createTimeStart, Date createTimeEnd);


    @Query(nativeQuery = true,value = "select sum(point) from user_points where user_id=?1")
    int getSumPointsByUid(Long uid);

    Page<UserPoints> findByUserId(Long userId, Pageable pageable);



    Page<UserPoints> findByUserIdAndPointsType(Long userId, PointsType pointsType, Pageable pageable);



}

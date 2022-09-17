package link.dwsy.ddl.repository.User;

import link.dwsy.ddl.XO.Enum.User.PointsType;
import link.dwsy.ddl.entity.User.UserPoints;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface UserPointsRepository extends JpaRepository<UserPoints, Long> {
    List<UserPoints> findByUserIdAndPointsTypeAndCreateTimeBetween(Long userId, PointsType pointsType, Date createTimeStart, Date createTimeEnd);



}

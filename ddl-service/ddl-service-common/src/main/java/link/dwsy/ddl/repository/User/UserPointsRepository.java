package link.dwsy.ddl.repository.User;

import link.dwsy.ddl.XO.Enum.User.PointsType;
import link.dwsy.ddl.XO.VO.HeatmapData;
import link.dwsy.ddl.entity.User.UserPoints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UserPointsRepository extends JpaRepository<UserPoints, Long> {
    List<UserPoints> findByUserIdAndPointsTypeAndCreateTimeBetween(Long userId, PointsType pointsType, Date createTimeStart, Date createTimeEnd);


    @Query(nativeQuery = true,value = "select sum(point) from user_points where user_id=?1")
    int getSumPointsByUid(Long uid);

    Page<UserPoints> findByUserId(Long userId, Pageable pageable);



    Page<UserPoints> findByUserIdAndPointsType(Long userId, PointsType pointsType, Pageable pageable);


    @Query(nativeQuery = true,
    value = "select date_trunc('day', date(create_time)) as date, count(point) as count \n" +
            "from user_points\n" +
            "where user_id = ?1\n" +
            "  and date_trunc('day', create_time) >= date_trunc('day', date(?2))\n" +
            "  and date_trunc('day', create_time) <= date_trunc('day', date(?3))\n" +
            "group BY date\n" +
            "order by date")
    List<Map<Object,Object>> getHeatmapDataByUserId(Long uid, LocalDate startDate, LocalDate endDate);


}

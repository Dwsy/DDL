package link.dwsy.ddl.mq.process;

import link.dwsy.ddl.XO.Enum.User.PointsType;
import link.dwsy.ddl.XO.Message.UserPointsMessage;
import link.dwsy.ddl.config.UserPointsLimit;
import link.dwsy.ddl.entity.User.UserPoints;
import link.dwsy.ddl.repository.User.UserPointsRepository;
import link.dwsy.ddl.util.DateUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class UserPointsProcess {
    @Resource
    UserPointsRepository userPointsRepository;


    public boolean pointsLimit(UserPointsMessage message) {
        PointsType pointsType = message.getPointsType();
        if (pointsType==PointsType.Reward) {
            return true;
        }
        Date tomorrowZero = DateUtil.getTomorrowZero();
        Date zero = DateUtil.getZero();
        List<UserPoints> pointsList =
                userPointsRepository.findByUserIdAndPointsTypeAndCreateTimeBetween(message.getUserId(), pointsType, zero, tomorrowZero);
        int sum=0;
        for (UserPoints points : pointsList) {
            sum += points.getPoint();
        }
        return sum < UserPointsLimit.get(pointsType);
    }
}

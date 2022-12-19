package link.dwsy.ddl.mq.process;

import org.springframework.stereotype.Service;

//@Service
public class UserPointsProcess {
//    @Resource
//    UserPointsRepository userPointsRepository;
//
//
//    public boolean pointsLimit(UserPointsMessage message) {
//        PointsType pointsType = message.getPointsType();
//        if (pointsType==PointsType.Reward) {
//            return true;
//        }
//        Date tomorrowZero = DateUtil.getTomorrowZero();
//        Date zero = DateUtil.getZero();
//        List<UserPoints> pointsList =
//                userPointsRepository.findByUserIdAndPointsTypeAndCreateTimeBetween(message.getUserId(), pointsType, zero, tomorrowZero);
//        int sum=0;
//        for (UserPoints points : pointsList) {
//            sum += points.getPoint();
//        }
//        return sum < UserPointsLimit.get(pointsType);
//    }
}

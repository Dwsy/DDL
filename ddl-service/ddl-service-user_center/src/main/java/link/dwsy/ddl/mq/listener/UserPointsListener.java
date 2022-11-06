package link.dwsy.ddl.mq.listener;


import link.dwsy.ddl.XO.Enum.User.PointsType;
import link.dwsy.ddl.XO.Message.UserPointsMessage;
import link.dwsy.ddl.entity.User.UserPoints;
import link.dwsy.ddl.constants.mq.UserPointsConstants;
import link.dwsy.ddl.mq.process.UserPointsProcess;
import link.dwsy.ddl.repository.User.UserPointsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/9/8
 */
@Component
@Slf4j
public class UserPointsListener {
    @Resource
    UserPointsRepository userPointsRepository;
    @Resource
    UserPointsProcess userPointsProcess;

    @RabbitListener(queues = UserPointsConstants.QUEUE_DDL_USER_POINTS)
    public void points(UserPointsMessage message) {
        System.out.println(11);
        if (!userPointsProcess.pointsLimit(message))
            return;

        Long userId = message.getUserId();
        PointsType pointsType = message.getPointsType();

        userPointsRepository.save(UserPoints.builder()
                .userId(userId)
                .point(pointsType.getPoint())
                .pointsType(pointsType)
                .build());
        log.info("用户{}积分记录{}成功", message.getUserId(), message.getPointsType().point);
    }
}

package link.dwsy.ddl.aspect;


import link.dwsy.ddl.XO.Enum.User.PointsType;
import link.dwsy.ddl.XO.Message.UserPointsMessage;
import link.dwsy.ddl.annotation.Points;
import link.dwsy.ddl.mq.UserPointsConstants;
import link.dwsy.ddl.support.UserSupport;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Aspect
@Slf4j
public class PointsAspect {


    @Resource
    UserSupport userSupport;

    @Resource
    RabbitTemplate rabbitTemplate;

    @Pointcut("@annotation(link.dwsy.ddl.annotation.Points)")
    public void cut() {}

    @After("cut() && @annotation(points)")
    public void dop(Points points) {
//        Long uid = userSupport.getCurrentUser().getId();
        var uid = 3L;
        PointsType type = points.TYPE();
        UserPointsMessage message = UserPointsMessage.builder().pointsType(type).userId(uid).build();
        rabbitTemplate.convertAndSend(UserPointsConstants.QUEUE_DDL_USER_POINTS, message);
    }
}

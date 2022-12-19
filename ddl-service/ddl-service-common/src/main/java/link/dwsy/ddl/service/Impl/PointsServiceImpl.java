package link.dwsy.ddl.service.Impl;

import link.dwsy.ddl.XO.Enum.User.PointsType;
import link.dwsy.ddl.XO.Message.UserPointsMessage;
import link.dwsy.ddl.annotation.Points;
import link.dwsy.ddl.config.UserPointsLimit;
import link.dwsy.ddl.constants.mq.UserPointsMQConstants;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.support.UserSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/12/19
 */
@Service
@Slf4j
public class PointsServiceImpl {

    @Resource
    private UserSupport userSupport;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    public void award(Points points) {
        LoginUserInfo currentUser = userSupport.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        Long userId = currentUser.getId();
        PointsType pointsType = points.TYPE();
        Long increment = redisTemplate.opsForHash().increment("user:points:" + userId, pointsType.toString(), pointsType.getPoint());
        int LimitPoints = UserPointsLimit.get(pointsType);
        if (increment >= LimitPoints) {
            log.info("用户{},{}>={}", userId, increment, LimitPoints);
            return;
        }
        redisTemplate.opsForSet().add("user:points:limit:idSet", userId.toString());
        UserPointsMessage message = UserPointsMessage
                .builder()
                .point(pointsType.getPoint())
                .pointsType(pointsType)
                .userId(userId)
                .build();
        rabbitTemplate.convertAndSend(UserPointsMQConstants.QUEUE_DDL_USER_POINTS, message);
    }

    public void customerAward(long userId,PointsType pointsType) {
//        LoginUserInfo currentUser = userSupport.getCurrentUser();
//        if (currentUser == null) {
//            return;
//        }
//        Long userId = currentUser.getId();
//        PointsType pointsType = points.TYPE();
        Long increment = redisTemplate.opsForHash().increment("user:points:" + userId, pointsType.toString(), pointsType.getPoint());
        int LimitPoints = UserPointsLimit.get(pointsType);
        if (increment >= LimitPoints) {
            log.info("用户{},{}>={}", userId, increment, LimitPoints);
            return;
        }
        redisTemplate.opsForSet().add("user:points:limit:idSet", String.valueOf(userId));
        UserPointsMessage message = UserPointsMessage
                .builder()
                .point(pointsType.getPoint())
                .pointsType(pointsType)
                .userId(userId)
                .build();
        rabbitTemplate.convertAndSend(UserPointsMQConstants.QUEUE_DDL_USER_POINTS, message);
    }


}

package link.dwsy.ddl.mq.listener;

import link.dwsy.ddl.XO.Message.UserActiveMessage;
import link.dwsy.ddl.constants.mq.UserActiveConstants;
import link.dwsy.ddl.mq.process.UserActiveProcess;
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
public class UserActiveListener {
    @Resource
    UserActiveProcess userActiveProcess;

    @RabbitListener(queues = UserActiveConstants.QUEUE_DDL_USER_ACTIVE)
    public void userActive(UserActiveMessage message) {
        userActiveProcess.ActiveLog(message.getUserActiveType(), message.getSourceId(), message.getUserId(),message.getUa());
        log.info("用户{}活跃记录{}成功", message.getUserId(), message.getUserActiveType().toString());
    }
}

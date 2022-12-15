package link.dwsy.ddl.mq.listener;

import link.dwsy.ddl.XO.Message.UserActiveMessage;
import link.dwsy.ddl.constants.mq.UserActiveMQConstants;
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

    @RabbitListener(queues = UserActiveMQConstants.QUEUE_DDL_USER_ACTIVE)
    public void userActive(UserActiveMessage message) {
        log.info("用户active消息:{}", message);
        userActiveProcess.ActiveLog(message.getUserActiveType(), message.getSourceId(), message.getUserId(),message.getUa());
    }
}

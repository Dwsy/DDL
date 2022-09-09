package link.dwsy.ddl.mq.listener;

import link.dwsy.ddl.XO.Message.UserActiveMessage;
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
public class History {
    @Resource
    UserActiveProcess userActiveProcess;

    @RabbitListener(queues = "history.user.active")
    public void userActive(UserActiveMessage message) {
        userActiveProcess.ActiveLog(message.getUserActiveType(), message.getSourceId(), message.getUserId());
        log.info("用户{}活跃记录{}成功", message.getUserId(), message.getUserActiveType().toString());
    }
}

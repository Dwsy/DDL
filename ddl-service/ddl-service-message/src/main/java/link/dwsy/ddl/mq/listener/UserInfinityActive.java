package link.dwsy.ddl.mq.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import link.dwsy.ddl.XO.Message.UserInfinityNotifyMessage;
import link.dwsy.ddl.constants.mq.UserActiveMQConstants;
import link.dwsy.ddl.mq.process.UserInfinityActiveProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/12/6
 */
@Component
@Slf4j
public class UserInfinityActive {
    @Resource
    private UserInfinityActiveProcess userInfinityActiveProcess;

    @RabbitListener(queues = UserActiveMQConstants.QUEUE_DDL_USER_INFINITY_ACTIVE)
    public void sendNotify(UserInfinityNotifyMessage message) throws JsonProcessingException {

        userInfinityActiveProcess.sendNotify(message);
    }

}

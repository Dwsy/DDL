package link.dwsy.ddl.mq.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import link.dwsy.ddl.XO.Message.InfinityMessage;
import link.dwsy.ddl.constants.mq.InfinityMQConstants;
import link.dwsy.ddl.mq.process.InfinityMessageProcess;
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
public class InfinityMessageListener {
    @Resource
    InfinityMessageProcess infinityMessageProcess;

    @RabbitListener(queues = InfinityMQConstants.QUEUE_DDL_INFINITY_SEND)
    public void sendInfinity(InfinityMessage message) throws JsonProcessingException {
        if (message.getRefId() > 0) {
            infinityMessageProcess.sendInfinity(message);
        }
    }
}

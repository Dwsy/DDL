package link.dwsy.ddl.mq.config;

import link.dwsy.ddl.constants.mq.InfinityMQConstants;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Dwsy
 * @Date 2022/9/11
 */

@Configuration
public class InfinityConfig {

    @Bean(InfinityMQConstants.QUEUE_DDL_INFINITY_SEND)
    public Queue infinitySendQueue() {
        return new Queue(InfinityMQConstants.QUEUE_DDL_INFINITY_SEND);
    }

}
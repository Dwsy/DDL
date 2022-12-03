package link.dwsy.ddl.mq.config;

import link.dwsy.ddl.constants.mq.UserPointsMQConstants;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Dwsy
 * @Date 2022/9/11
 */

@Configuration
public class UserPointsConfig {

    @Bean(UserPointsMQConstants.QUEUE_DDL_USER_POINTS)
    public Queue pointsQueue() {
        return new Queue(UserPointsMQConstants.QUEUE_DDL_USER_POINTS);
    }

}
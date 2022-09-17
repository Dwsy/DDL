package link.dwsy.ddl.mq.config;

import link.dwsy.ddl.mq.UserPointsConstants;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Dwsy
 * @Date 2022/9/11
 */

@Configuration
public class UserPointsConfig {

    @Bean(UserPointsConstants.QUEUE_DDL_USER_POINTS)
    public Queue pointsQueue() {
        return new Queue(UserPointsConstants.QUEUE_DDL_USER_POINTS);
    }

}
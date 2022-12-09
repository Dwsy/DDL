package link.dwsy.ddl.mq.config;

import link.dwsy.ddl.constants.mq.UserActiveMQConstants;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Dwsy
 * @Date 2022/9/11
 */

@Configuration
public class UserInfinityActiveConfig {



    @Bean(UserActiveMQConstants.QUEUE_DDL_USER_INFINITY_ACTIVE)
    public Queue UserInfinityActiveQuestion() {
        return new Queue(UserActiveMQConstants.QUEUE_DDL_USER_INFINITY_ACTIVE, true);
    }

}
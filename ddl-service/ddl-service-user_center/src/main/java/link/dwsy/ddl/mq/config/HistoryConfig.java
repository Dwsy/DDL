package link.dwsy.ddl.mq.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Dwsy
 * @Date 2022/9/11
 */

@Configuration
public class HistoryConfig {

    @Bean("history.user.active")
    public Queue activeQueue() {
        return new Queue("history.user.active");
    }

}
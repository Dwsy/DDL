package link.dwsy.ddl.mq.config;

import link.dwsy.ddl.mq.UserActiveConstants;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Dwsy
 * @Date 2022/9/11
 */

@Configuration
public class UserQuestionAnswerActiveConfig {

    @Bean(UserActiveConstants.QUEUE_DDL_USER_ARTICLE_COMMENT_ACTIVE)
    public Queue qaActiveQueue() {
        return new Queue(UserActiveConstants.QUEUE_DDL_USER_QUESTION_ANSWER_OR_COMMENT_ACTIVE);
    }

}
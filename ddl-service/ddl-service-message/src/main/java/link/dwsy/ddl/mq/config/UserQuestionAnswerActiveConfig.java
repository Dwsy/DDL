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
public class UserQuestionAnswerActiveConfig {

    @Bean(UserActiveMQConstants.QUEUE_DDL_USER_QUESTION_ANSWER_OR_COMMENT_ACTIVE)
    public Queue qaActiveQueue() {
        return new Queue(UserActiveMQConstants.QUEUE_DDL_USER_QUESTION_ANSWER_OR_COMMENT_ACTIVE, true);
    }

    @Bean(UserActiveMQConstants.QUEUE_DDL_USER_INVITATION_USER_ANSWER_QUESTION)
    public Queue invitationUserAnswerQuestion() {
        return new Queue(UserActiveMQConstants.QUEUE_DDL_USER_INVITATION_USER_ANSWER_QUESTION, true);
    }

}
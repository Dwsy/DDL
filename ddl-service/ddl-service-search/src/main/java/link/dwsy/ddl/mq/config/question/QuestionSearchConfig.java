package link.dwsy.ddl.mq.config.question;

import link.dwsy.ddl.mq.QuestionSearchConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Dwsy
 * @Date 2022/9/11
 */

@Configuration
public class QuestionSearchConfig {

    @Bean
    public TopicExchange topicExchange() {

        return new TopicExchange(QuestionSearchConstants.EXCHANGE_DDL_QUESTION_SEARCH, true, false);
    }

    @Bean
    public Queue createQueue() {
        return new Queue(QuestionSearchConstants.QUEUE_DDL_QUESTION_SEARCH_CREATE, true);
    }

    @Bean
    public Queue updateQueue() {
        return new Queue(QuestionSearchConstants.QUEUE_DDL_QUESTION_SEARCH_UPDATE, true);
    }

    @Bean
    public Queue updateScoreQueue() {
        return new Queue(QuestionSearchConstants.QUEUE_DDL_QUESTION_SEARCH_UPDATE_SCORE, true);
    }

    @Bean
    public Queue deleteQueue() {
        return new Queue(QuestionSearchConstants.QUEUE_DDL_QUESTION_SEARCH_DELETE, true);
    }


    @Bean
    public Binding createQueueBinding() {
        return BindingBuilder.bind(createQueue()).to(topicExchange()).with(QuestionSearchConstants.RK_DDL_QUESTION_SEARCH_CREATE);
    }

    @Bean
    public Binding deleteQueueBinding() {
        return BindingBuilder.bind(deleteQueue()).to(topicExchange()).with(QuestionSearchConstants.RK_DDL_QUESTION_SEARCH_DELETE);
    }

    @Bean
    public Binding updateQueueBinding() {
        return BindingBuilder.bind(createQueue()).to(topicExchange()).with(QuestionSearchConstants.RK_DDL_QUESTION_SEARCH_UPDATE);
    }

    @Bean
    public Binding updateScoreBinding() {
        return BindingBuilder.bind(deleteQueue()).to(topicExchange()).with(QuestionSearchConstants.RK_DDL_QUESTION_SEARCH_UPDATE_SCORE);
    }

}
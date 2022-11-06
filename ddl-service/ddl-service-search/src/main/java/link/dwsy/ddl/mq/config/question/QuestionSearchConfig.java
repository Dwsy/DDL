package link.dwsy.ddl.mq.config.question;

import link.dwsy.ddl.constants.mq.QuestionSearchConstants;
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
    public TopicExchange questionSearchTopicExchange() {

        return new TopicExchange(QuestionSearchConstants.EXCHANGE_DDL_QUESTION_SEARCH, true, false);
    }

    @Bean
    public Queue questionCreateQueue() {
        return new Queue(QuestionSearchConstants.QUEUE_DDL_QUESTION_SEARCH_CREATE, true);
    }

    @Bean
    public Queue questionUpdateQueue() {
        return new Queue(QuestionSearchConstants.QUEUE_DDL_QUESTION_SEARCH_UPDATE, true);
    }

    @Bean
    public Queue questionUpdateScoreQueue() {
        return new Queue(QuestionSearchConstants.QUEUE_DDL_QUESTION_SEARCH_UPDATE_SCORE, true);
    }

    @Bean
    public Queue questionDeleteQueue() {
        return new Queue(QuestionSearchConstants.QUEUE_DDL_QUESTION_SEARCH_DELETE, true);
    }


    @Bean
    public Binding questionCreateQueueBinding() {
        return BindingBuilder.bind(questionCreateQueue()).to(questionSearchTopicExchange()).with(QuestionSearchConstants.RK_DDL_QUESTION_SEARCH_CREATE);
    }

    @Bean
    public Binding questionDeleteQueueBinding() {
        return BindingBuilder.bind(questionDeleteQueue()).to(questionSearchTopicExchange()).with(QuestionSearchConstants.RK_DDL_QUESTION_SEARCH_DELETE);
    }

    @Bean
    public Binding questionUpdateQueueBinding() {
        return BindingBuilder.bind(questionCreateQueue()).to(questionSearchTopicExchange()).with(QuestionSearchConstants.RK_DDL_QUESTION_SEARCH_UPDATE);
    }

    @Bean
    public Binding questionUpdateScoreBinding() {
        return BindingBuilder.bind(questionDeleteQueue()).to(questionSearchTopicExchange()).with(QuestionSearchConstants.RK_DDL_QUESTION_SEARCH_UPDATE_SCORE);
    }

}
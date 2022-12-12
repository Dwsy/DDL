package link.dwsy.ddl.mq.config.infinity;

import link.dwsy.ddl.constants.mq.InfinityTopicMQConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Dwsy
 * @Date 2022/12/12
 */
@Configuration
public class InfinityTopicSearchConfig {
    @Bean
    public TopicExchange infinityTopicSearchExchange() {

        return new TopicExchange(InfinityTopicMQConstants.EXCHANGE_DDL_INFINITY_TOPIC, true, false);
    }

    @Bean
    public Queue infinityTopicSearchCreateQueue() {
        return new Queue(InfinityTopicMQConstants.QUEUE_DDL_INFINITY_TOPIC_SEARCH_CREATE, true);
    }

    @Bean
    public Queue infinityTopicSearchUpdateQueue() {
        return new Queue(InfinityTopicMQConstants.QUEUE_DDL_INFINITY_TOPIC_SEARCH_UPDATE, true);
    }

    @Bean
    public Queue infinityTopicSearchUpdateScoreQueue() {
        return new Queue(InfinityTopicMQConstants.QUEUE_DDL_INFINITY_TOPIC_SEARCH_UPDATE_SCORE, true);
    }

    @Bean
    public Queue infinityTopicSearchDeleteQueue() {
        return new Queue(InfinityTopicMQConstants.QUEUE_DDL_INFINITY_TOPIC_SEARCH_DELETE, true);
    }


    @Bean
    public Binding infinityTopicSearchCreateQueueBinding() {
        return BindingBuilder.bind(infinityTopicSearchCreateQueue())
                .to(infinityTopicSearchExchange()).with(InfinityTopicMQConstants.RK_DDL_INFINITY_TOPIC_SEARCH_CREATE);
    }

    @Bean
    public Binding infinityTopicSearchDeleteQueueBinding() {
        return BindingBuilder.bind(infinityTopicSearchDeleteQueue())
                .to(infinityTopicSearchExchange()).with(InfinityTopicMQConstants.RK_DDL_INFINITY_TOPIC_SEARCH_DELETE);
    }

    @Bean
    public Binding infinityTopicSearchUpdateQueueBinding() {
        return BindingBuilder.bind(infinityTopicSearchCreateQueue())
                .to(infinityTopicSearchExchange()).with(InfinityTopicMQConstants.RK_DDL_INFINITY_TOPIC_SEARCH_UPDATE);
    }

    @Bean
    public Binding infinityTopicSearchUpdateScoreBinding() {
        return BindingBuilder.bind(infinityTopicSearchUpdateScoreQueue())
                .to(infinityTopicSearchExchange()).with(InfinityTopicMQConstants.RK_DDL_INFINITY_TOPIC_SEARCH_UPDATE_SCORE);
    }
}

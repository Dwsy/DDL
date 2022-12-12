//package link.dwsy.ddl.mq.config.infinity;
//
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.core.TopicExchange;
//import org.springframework.context.annotation.Bean;
//
///**
// * @Author Dwsy
// * @Date 2022/12/12
// */
////@Configuration
//public class InfinitySearchConfig {
//    public TopicExchange infinitySearchTopicExchange() {
//
//        return new TopicExchange(InfinitySearchMQConstants.EXCHANGE_DDL_INFINITY_SEARCH, true, false);
//    }
//
//    @Bean
//    public Queue infinitySearchCreateQueue() {
//        return new Queue(InfinitySearchMQConstants.QUEUE_DDL_INFINITY_SEARCH_CREATE, true);
//    }
//
//    @Bean
//    public Queue infinitySearchUpdateQueue() {
//        return new Queue(InfinitySearchMQConstants.QUEUE_DDL_INFINITY_SEARCH_UPDATE, true);
//    }
//
//    @Bean
//    public Queue infinitySearchUpdateScoreQueue() {
//        return new Queue(InfinitySearchMQConstants.QUEUE_DDL_INFINITY_SEARCH_UPDATE_SCORE, true);
//    }
//
//    @Bean
//    public Queue infinitySearchDeleteQueue() {
//        return new Queue(InfinitySearchMQConstants.QUEUE_DDL_INFINITY_SEARCH_DELETE, true);
//    }
//
//
//    @Bean
//    public Binding infinitySearchCreateQueueBinding() {
//        return BindingBuilder.bind(infinitySearchCreateQueue())
//                .to(infinitySearchTopicExchange()).with(InfinitySearchMQConstants.RK_DDL_INFINITY_SEARCH_CREATE);
//    }
//
//    @Bean
//    public Binding deleteQueueBinding() {
//        return BindingBuilder.bind(infinitySearchDeleteQueue())
//                .to(infinitySearchTopicExchange()).with(InfinitySearchMQConstants.RK_DDL_INFINITY_SEARCH_DELETE);
//    }
//
//    @Bean
//    public Binding updateQueueBinding() {
//        return BindingBuilder.bind(infinitySearchCreateQueue())
//                .to(infinitySearchTopicExchange()).with(InfinitySearchMQConstants.RK_DDL_INFINITY_SEARCH_UPDATE);
//    }
//
//    @Bean
//    public Binding updateScoreBinding() {
//        return BindingBuilder.bind(infinitySearchUpdateScoreQueue())
//                .to(infinitySearchTopicExchange()).with(InfinitySearchMQConstants.RK_DDL_INFINITY_SEARCH_UPDATE_SCORE);
//    }
//}

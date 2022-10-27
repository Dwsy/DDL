package link.dwsy.ddl.mq.config.article;

import link.dwsy.ddl.mq.ArticleSearchConstants;
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
public class ArticleSearchConfig {

    @Bean
    public TopicExchange articleSearchTopicExchange() {

        return new TopicExchange(ArticleSearchConstants.EXCHANGE_DDL_ARTICLE_SEARCH, true, false);
    }

    @Bean
    public Queue articleSearchCreateQueue() {
        return new Queue(ArticleSearchConstants.QUEUE_DDL_ARTICLE_SEARCH_CREATE, true);
    }

    @Bean
    public Queue articleSearchUpdateQueue() {
        return new Queue(ArticleSearchConstants.QUEUE_DDL_ARTICLE_SEARCH_UPDATE, true);
    }

    @Bean
    public Queue articleSearchUpdateScoreQueue() {
        return new Queue(ArticleSearchConstants.QUEUE_DDL_ARTICLE_SEARCH_UPDATE_SCORE, true);
    }

    @Bean
    public Queue articleSearchDeleteQueue() {
        return new Queue(ArticleSearchConstants.QUEUE_DDL_ARTICLE_SEARCH_DELETE, true);
    }


    @Bean
    public Binding articleSearchCreateQueueBinding() {
        return BindingBuilder.bind(articleSearchCreateQueue()).to(articleSearchTopicExchange()).with(ArticleSearchConstants.RK_DDL_ARTICLE_SEARCH_CREATE);
    }

    @Bean
    public Binding deleteQueueBinding() {
        return BindingBuilder.bind(articleSearchDeleteQueue()).to(articleSearchTopicExchange()).with(ArticleSearchConstants.RK_DDL_ARTICLE_SEARCH_DELETE);
    }

    @Bean
    public Binding updateQueueBinding() {
        return BindingBuilder.bind(articleSearchCreateQueue()).to(articleSearchTopicExchange()).with(ArticleSearchConstants.RK_DDL_ARTICLE_SEARCH_UPDATE);
    }

    @Bean
    public Binding updateScoreBinding() {
        return BindingBuilder.bind(articleSearchDeleteQueue()).to(articleSearchTopicExchange()).with(ArticleSearchConstants.RK_DDL_ARTICLE_SEARCH_UPDATE_SCORE);
    }

}
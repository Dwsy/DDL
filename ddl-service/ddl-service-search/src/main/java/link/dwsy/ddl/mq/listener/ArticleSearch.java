package link.dwsy.ddl.mq.listener;

import link.dwsy.ddl.mq.ArticleSearchConstants;
import link.dwsy.ddl.mq.process.ArticleSearchProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/9/11
 */
@Component
@Slf4j
public class ArticleSearch {
    @Resource
    ArticleSearchProcess articleSearchProcess;

    @RabbitListener(queues = ArticleSearchConstants.QUEUE_DDL_ARTICLE_SEARCH_UPDATE_SCORE)
    public void updateScore(Long articleId) {
        if (articleId != null) {
            if (articleSearchProcess.updateScoreDataById(articleId)) {
                log.info("doc部分字段{}更新成功", articleId);
            }
        }

    }

    @RabbitListener(queues = {ArticleSearchConstants.QUEUE_DDL_ARTICLE_SEARCH_UPDATE, ArticleSearchConstants.QUEUE_DDL_ARTICLE_SEARCH_CREATE})
    public void updateAllDataById(Long articleId) {
        if (articleId != null) {
            if (articleSearchProcess.updateOrSaveAllDataById(articleId)) {
                log.info("doc{}新增或更新成功", articleId);
            }
        }
    }

    @RabbitListener(queues = ArticleSearchConstants.QUEUE_DDL_ARTICLE_SEARCH_DELETE)
    public void delById(Long articleId) {
        if (articleId != null) {
            if (articleSearchProcess.delDocById(articleId)) {
                log.info("doc{}删除成功", articleId);
            }
        }
    }
}

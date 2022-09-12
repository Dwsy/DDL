package link.dwsy.ddl.mq.listener;

import link.dwsy.ddl.mq.ArticleSearchConstants;
import link.dwsy.ddl.mq.process.ArticleSearchProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Author Dwsy
 * @Date 2022/9/11
 */
@Component
@Slf4j
public class ArticleSearch {

    public final String scoreKey = "article:search:update:score";
    public final int bufferSize = 100;
    @Resource
    ArticleSearchProcess articleSearchProcess;
    @Resource
    RedisTemplate<String, String> redisTemplate;

    @RabbitListener(queues = ArticleSearchConstants.QUEUE_DDL_ARTICLE_SEARCH_UPDATE_SCORE)
    public void updateScore(Long articleId) {
        if (articleId != null) {

            Long size = redisTemplate.opsForValue().increment(scoreKey + "num");
            redisTemplate.opsForSet().add(scoreKey, articleId.toString());
            String timeOut = redisTemplate.opsForValue().get(scoreKey + "time:out");
            System.out.println("timeOut"+timeOut);

            if (timeOut == null) {
                if (size != null && size >= bufferSize/10) {
                    size= (long) bufferSize;
                    redisTemplate.delete(scoreKey + "time:out");
                }
            }

            if (size != null) {
                if (size >= bufferSize) {
                    Boolean lock = redisTemplate.opsForValue().setIfAbsent(scoreKey + "lock", "1", 60, TimeUnit.SECONDS);
                    if (Boolean.TRUE.equals(lock)) {
                        Set<String> articleIds = redisTemplate.opsForSet().members(scoreKey);
                        redisTemplate.delete(scoreKey);
                        redisTemplate.delete(scoreKey + "num");
                        assert articleIds != null;
                        articleIds.stream().map(Long::parseLong).forEach(articleSearchProcess::updateScoreDataById);
                        redisTemplate.delete(scoreKey + "lock");
                        log.info("article doc:{} score字段更新成功", articleIds);
                    } else {
                        redisTemplate.opsForSet().add(scoreKey, articleId.toString());
                    }

                } else {
                    redisTemplate.opsForValue().set(scoreKey + "time:out", "1", 10, TimeUnit.SECONDS);
                    log.info("+1 QUEUE_DDL_ARTICLE_SEARCH_UPDATE_SCORE");
                    redisTemplate.opsForSet().add(scoreKey, articleId.toString());
                }
            }
        }

    }

    @RabbitListener(queues = {
            ArticleSearchConstants.QUEUE_DDL_ARTICLE_SEARCH_UPDATE,
            ArticleSearchConstants.QUEUE_DDL_ARTICLE_SEARCH_CREATE})
    public void updateAllDataById(Long articleId) {
        if (articleId != null) {
            if (articleSearchProcess.updateOrSaveAllDataById(articleId)) {
                log.info("article doc:{}新增或更新成功", articleId);
            }
        }
    }

    @RabbitListener(queues = ArticleSearchConstants.QUEUE_DDL_ARTICLE_SEARCH_DELETE)
    public void delById(Long articleId) {
        if (articleId != null) {
            if (articleSearchProcess.delDocById(articleId)) {
                log.info("article doc:{}删除成功", articleId);
            }
        }
    }
}

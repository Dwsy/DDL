package link.dwsy.ddl.mq.listener.question;

import link.dwsy.ddl.constants.mq.ArticleSearchConstants;
import link.dwsy.ddl.constants.mq.QuestionSearchConstants;
import link.dwsy.ddl.mq.process.article.ArticleSearchProcess;
import link.dwsy.ddl.mq.process.question.QuestionSearchProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Author Dwsy
 * @Date 2022/9/11
 */
@Component
@Slf4j
public class QuestionSearch {
    public final String scoreKey = QuestionSearchConstants.RK_DDL_QUESTION_SEARCH_UPDATE_SCORE;
    //    public final int bufferSize = 10;
    @Resource
    private ArticleSearchProcess articleSearchProcess;

    @Resource
    private QuestionSearchProcess questionSearchProcess;
    @Resource
    private RedisTemplate<String, String> redisTemplate;


    @RabbitListener(queues = {
            QuestionSearchConstants.QUEUE_DDL_QUESTION_SEARCH_UPDATE,
            QuestionSearchConstants.QUEUE_DDL_QUESTION_SEARCH_CREATE
    })
    public void updateAllDataById(Long questionId) {
        if (questionId != null && questionId > 0) {
            if (questionSearchProcess.updateOrSaveAllDataById(questionId)) {
                log.info("question doc:{}新增或更新成功", questionId);
            } else {
                log.info("question doc:{}新增或更新失败", questionId);
            }
        }
    }

    @RabbitListener(queues = QuestionSearchConstants.QUEUE_DDL_QUESTION_SEARCH_DELETE)
    public void delById(Long questionId) {
        if (questionId != null && questionId > 0) {
            if (articleSearchProcess.delDocById(questionId)) {
                log.info("question doc:{}删除成功", questionId);
            }
        }
    }


    @RabbitListener(queues = ArticleSearchConstants.QUEUE_DDL_ARTICLE_SEARCH_UPDATE_SCORE)
    public void updateScore(Long questionId) {
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(scoreKey + "lock", String.valueOf(questionId), 600, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(lock)) {
            questionSearchProcess.updateScoreDataById(questionId);
            redisTemplate.delete(scoreKey + "lock");
            log.info("question doc:{} score字段更新成功", questionId);
        }
//        if (questionId != null) {
//
//            Long size = redisTemplate.opsForValue().increment(scoreKey + "num");
//            redisTemplate.opsForSet().add(scoreKey, questionId.toString());
//            String timeOut = redisTemplate.opsForValue().get(scoreKey + "time:out");
//            System.out.println("timeOut" + timeOut);
//
//            if (timeOut == null) {
//                if (size != null && size >= bufferSize / 10) {
//                    size = (long) bufferSize;
//                    redisTemplate.delete(scoreKey + "time:out");
//                }
//            }
//
//
//            if (size != null && size >= bufferSize) {
//                Boolean lock = redisTemplate.opsForValue().setIfAbsent(scoreKey + "lock", "1", 60, TimeUnit.SECONDS);
//                if (Boolean.TRUE.equals(lock)) {
//                    Set<String> articleIds = redisTemplate.opsForSet().members(scoreKey);
//                    redisTemplate.delete(scoreKey);
//                    redisTemplate.delete(scoreKey + "num");
//                    assert articleIds != null;
//                    articleIds.stream().map(Long::parseLong).forEach(articleSearchProcess::updateScoreDataById);
//                    redisTemplate.delete(scoreKey + "lock");
//                    log.info("article doc:{} score字段更新成功", articleIds);
//                } else {
//                    redisTemplate.opsForSet().add(scoreKey, questionId.toString());
//                }
//
//            } else {
//                redisTemplate.opsForValue().set(scoreKey + "time:out", "1", 10, TimeUnit.SECONDS);
//                log.info("bufferSize:{} QUEUE_DDL_ARTICLE_SEARCH_UPDATE_SCORE", size);
//                redisTemplate.opsForSet().add(scoreKey, questionId.toString());
//            }
//
//        }

    }


}

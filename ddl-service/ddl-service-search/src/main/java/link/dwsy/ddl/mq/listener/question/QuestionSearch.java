package link.dwsy.ddl.mq.listener.question;

import link.dwsy.ddl.constants.mq.QuestionSearchMQConstants;
import link.dwsy.ddl.mq.process.question.QuestionSearchProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/9/11
 */
@Component
@Slf4j
public class QuestionSearch {
    public final String scoreKey = QuestionSearchMQConstants.RK_DDL_QUESTION_SEARCH_UPDATE_SCORE;

    @Resource
    private QuestionSearchProcess questionSearchProcess;
    @Resource
    private RedisTemplate<String, String> redisTemplate;


    @RabbitListener(queues = {
            QuestionSearchMQConstants.QUEUE_DDL_QUESTION_SEARCH_UPDATE,
            QuestionSearchMQConstants.QUEUE_DDL_QUESTION_SEARCH_CREATE
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

    @RabbitListener(queues = QuestionSearchMQConstants.QUEUE_DDL_QUESTION_SEARCH_DELETE)
    public void delById(Long questionId) {
        if (questionId != null && questionId > 0) {
            if (questionSearchProcess.delDocById(questionId)) {
                log.info("question doc:{}删除成功", questionId);
            }
        }
    }


    @RabbitListener(queues = QuestionSearchMQConstants.QUEUE_DDL_QUESTION_SEARCH_UPDATE_SCORE)
    public void updateScore(Long questionId) {

        questionSearchProcess.updateScoreDataById(questionId);
        log.info("question doc:{} score字段更新成功", questionId);


    }


}

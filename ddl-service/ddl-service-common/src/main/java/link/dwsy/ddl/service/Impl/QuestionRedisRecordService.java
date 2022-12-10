package link.dwsy.ddl.service.Impl;

import link.dwsy.ddl.constants.mq.QuestionSearchMQConstants;
import link.dwsy.ddl.constants.task.RedisRecordHashKey;
import link.dwsy.ddl.constants.task.RedisRecordKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Author Dwsy
 * @Date 2022/12/9
 */

@Service
@Slf4j
public class QuestionRedisRecordService {

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;


    public void record(Long id, RedisRecordHashKey recordHashKey, int increment) {
        if (recordHashKey == RedisRecordHashKey.view) {
            redisTemplate.opsForSet().add(RedisRecordKey.RedisQuestionRecordKey, String.valueOf(id));
        }
        redisTemplate.opsForHash().increment(RedisRecordKey.RedisQuestionRecordKey + id, recordHashKey.toString(), increment);
        if (increment <= 0) {
            return;
        }
        String num = (String) redisTemplate.opsForHash().get(RedisRecordKey.RedisQuestionRecordKey + id, recordHashKey.toString());
        if (num != null && (Integer.parseInt(num)) % 10 == 0) {

            Boolean lock = redisTemplate.opsForValue().setIfAbsent
                    (QuestionSearchMQConstants.RK_DDL_QUESTION_SEARCH_UPDATE_SCORE + "lock", String.valueOf(id), 600, TimeUnit.SECONDS);

            if (Boolean.TRUE.equals(lock)) {
                log.info("RK_DDL_ARTICLE_SEARCH_UPDATE_SCORE:{}", id);

                rabbitTemplate.convertAndSend
                        (QuestionSearchMQConstants.EXCHANGE_DDL_QUESTION_SEARCH,
                                QuestionSearchMQConstants.RK_DDL_QUESTION_SEARCH_UPDATE_SCORE, id);
            }
        }
    }
}

package link.dwsy.ddl.task;

import link.dwsy.ddl.constants.task.RedisRecordKey;
import link.dwsy.ddl.entity.Data.Question.QuestionDailyData;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import link.dwsy.ddl.repository.Data.Question.QuestionDailyDataRepository;
import link.dwsy.ddl.repository.QA.QaQuestionFieldRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class QuestionDailyDataTask {

    @Resource
    private QaQuestionFieldRepository qaQuestionFieldRepository;

    @Resource
    private QuestionDailyDataRepository questionDailyDataRepository;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;


    @Scheduled(cron = "0 5 0 * * ? ")
//    @Scheduled(cron = "0 * * * * ? ")
    public void record() {
        log.info("定时任务执行{}", Thread.currentThread().getName());
        Set<Object> QuestionIdSet = redisTemplate.opsForHash().keys(RedisRecordKey.RedisQuestionRecordKey);
        if (QuestionIdSet.size() == 0) {
            log.info("今日无访问");
            return;
        }
        List<QaQuestionField> questionFields = qaQuestionFieldRepository.findAllById
                (QuestionIdSet.stream().map(q-> Long.parseLong(q.toString())).collect(Collectors.toList()));
        ArrayList<QuestionDailyData> dailyDataArrayList = new ArrayList<>(questionFields.size());
        for (QaQuestionField questionField : questionFields) {
            QuestionDailyData data = QuestionDailyData.builder()
                    .questionFieldId(questionField.getId())
                    .upNum(questionField.getUpNum())
                    .downNum(questionField.getDownNum())
                    .user(questionField.getUser())
                    .title(questionField.getTitle())
                    .answerNum(questionField.getAnswerNum())
                    .collectNum(questionField.getCollectNum())
                    .viewNum(questionField.getViewNum())
                    .build();
            dailyDataArrayList.add(data);
        }
        questionDailyDataRepository.saveAll(dailyDataArrayList);
        redisTemplate.delete(RedisRecordKey.RedisQuestionRecordKey);
    }
}

package link.dwsy.ddl.task;

import link.dwsy.ddl.constants.task.RedisRecordHashKey;
import link.dwsy.ddl.constants.task.RedisRecordKey;
import link.dwsy.ddl.entity.Data.Question.QuestionDailyData;
import link.dwsy.ddl.repository.Data.Question.QuestionDailyDataRepository;
import link.dwsy.ddl.repository.QA.QaQuestionFieldRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

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
        Set<String> idSet = redisTemplate.opsForSet().members(RedisRecordKey.RedisQuestionRecordKey);
        if (idSet != null) {
            if (idSet.size() == 0) {
                log.info("今日无访问");
                return;
            }
            ArrayList<QuestionDailyData> dailyDataArrayList = new ArrayList<>(idSet.size());
            for (String id : idSet) {
                Map<Object, Object> dataMap = redisTemplate.opsForHash().entries(RedisRecordKey.RedisQuestionRecordKey + id);
                if (dataMap.isEmpty()) {
                    return;
                }
                int view = getMapValue(dataMap, RedisRecordHashKey.view);
                int up = getMapValue(dataMap, RedisRecordHashKey.up);
                int down = getMapValue(dataMap, RedisRecordHashKey.down);
                int collect = getMapValue(dataMap, RedisRecordHashKey.collect);
                int comment = getMapValue(dataMap, RedisRecordHashKey.comment);
                int answer = getMapValue(dataMap, RedisRecordHashKey.answer);
                int score = 0;
                score += view;
                score += up * 5;
                score -= down * 10;
                score += collect * 30;
                score += comment * 10;
                score += answer * 50;

                long questionId = Long.parseLong(id);
                QuestionDailyData dailyData = QuestionDailyData.builder()
                        .userId(qaQuestionFieldRepository.getUserIdByQuestionId(questionId))
                        .questionFieldId(questionId)
                        .upNum(up)
                        .downNum(down)
                        .commentNum(comment)
                        .viewNum(view)
                        .collectNum(collect)
                        .answerNum(answer)
                        .score(score)
                        .dataDate(LocalDate.now())
                        .groupId(qaQuestionFieldRepository.getGroupIdByQuestionId(questionId))
                        .tagIds(qaQuestionFieldRepository.getTagIdListByQuestionId(questionId))
                        .build();
                dailyDataArrayList.add(dailyData);
            }
            for (String id : idSet) {
                redisTemplate.delete(RedisRecordKey.RedisArticleRecordKey + id);
            }
            questionDailyDataRepository.saveAll(dailyDataArrayList);
            redisTemplate.delete(RedisRecordKey.RedisArticleRecordKey);
        }

    }

    private int getMapValue(Map<Object, Object> map, RedisRecordHashKey key) {
        if (map.get(key.toString()) == null) {
            return 0;
        }
        return Integer.parseInt((String) map.get(key.toString()));
    }
}

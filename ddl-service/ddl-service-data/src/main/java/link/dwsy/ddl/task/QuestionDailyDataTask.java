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
        Set<String> idSet = redisTemplate.opsForSet().members(RedisRecordKey.RedisArticleRecordKey);
        if (idSet != null) {
            if (idSet.size() == 0) {
                log.info("今日无访问");
                return;
            }
            ArrayList<QuestionDailyData> dailyDataArrayList = new ArrayList<>(idSet.size());
            for (String id : idSet) {
                Map<Object, Object> dataMap = redisTemplate.opsForHash().entries(RedisRecordKey.RedisArticleRecordKey + id);
                Integer view = (Integer) dataMap.get(RedisRecordHashKey.view);
                Integer up = (Integer) dataMap.get(RedisRecordHashKey.up);
                Integer down = (Integer) dataMap.get(RedisRecordHashKey.down);
                Integer collect = (Integer) dataMap.get(RedisRecordHashKey.collect);
                Integer answer = (Integer) dataMap.get(RedisRecordHashKey.answer);
                Integer comment = (Integer) dataMap.get(RedisRecordHashKey.comment);
                int score = 0;
                if (view != null) {
                    score += view;
                }
                if (up != null) {
                    score += up*5;
                }
                if (down != null) {
                    score -= down*10;
                }
                if (collect != null) {
                    score += collect*30;
                }
                if (comment != null) {
                    score += comment*10;
                }
                if (answer != null) {
                    score += answer*50;
                }
                QuestionDailyData dailyData = QuestionDailyData.builder()
                        .questionFieldId(Long.parseLong(id))
                        .viewNum(view == null ? 0 : view)
                        .upNum(up == null ? 0 : up)
                        .downNum(down == null ? 0 : down)
                        .collectNum(collect == null ? 0 : collect)
                        .answerNum(answer == null ? 0 : answer)
                        .commentNum(comment == null ? 0 : comment)
                        .score(score)
                        .date(LocalDate.now())
                        .build();
                dailyDataArrayList.add(dailyData);
                redisTemplate.delete(RedisRecordKey.RedisArticleRecordKey + id);
            }
            questionDailyDataRepository.saveAll(dailyDataArrayList);
            redisTemplate.delete(RedisRecordKey.RedisArticleRecordKey);
        }

    }
}

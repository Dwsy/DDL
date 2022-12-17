package link.dwsy.ddl.task;

import link.dwsy.ddl.constants.task.RedisRecordHashKey;
import link.dwsy.ddl.constants.task.RedisRecordKey;
import link.dwsy.ddl.entity.Data.Question.QuestionDailyData;
import link.dwsy.ddl.repository.Data.Question.QuestionDailyDataRepository;
import link.dwsy.ddl.repository.QA.QaQuestionFieldRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class QuestionDailyDataTask {

    @Resource
    private QaQuestionFieldRepository qaQuestionFieldRepository;

    @Resource
    private QuestionDailyDataRepository questionDailyDataRepository;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;


    @Scheduled(cron = "0 2 0 * * ? ")
//    @Scheduled(cron = "0 * * * * ? ")
    public void record() {
        log.info("定时任务执行{}", Thread.currentThread().getName());

        Set<ZSetOperations.TypedTuple<String>> valueScoreSet = null;
        try {
            valueScoreSet =
                    redisTemplate.opsForZSet().reverseRangeWithScores(RedisRecordKey.RedisArticleRecordToDayKey, 0, - 1);
        }catch (Exception e){
            log.error("定时任务执行异常或无访问数据", e);
        }
        if (valueScoreSet==null) {
            log.info("今日无访问");
            return;
        }
        ArrayList<String> delKeyList = new ArrayList<>();
        ArrayList<QuestionDailyData> dailyDataArrayList = new ArrayList<>(valueScoreSet.size());
//        ArrayList<String> idList = new ArrayList<>();
        //        CopyOnWriteArrayList<String> delKeyList = new CopyOnWriteArrayList<>();
        //todo 多线程处理
//        valueScoreSet.forEach(p -> {
//            String id = p.getValue();
//            if (id == null) {
//                return;
//            }
//            idList.add(id);
//        });
//        List<List<String>> partition = Lists.partition(idList, 1000);
//        for (List<String> idsList : partition) {
//            idsList.forEach(id->{
////                code..
//            });
//        }
        valueScoreSet.forEach(p -> {
            String id = p.getValue();
            if (id==null) {
                return;
            }
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
            delKeyList.add(RedisRecordKey.RedisQuestionRecordKey + id);
        });

        questionDailyDataRepository.saveAll(dailyDataArrayList);
        redisTemplate.delete(delKeyList);
        redisTemplate.delete(RedisRecordKey.RedisQuestionRecordToDayKey);
    }

    private int getMapValue(Map<Object, Object> map, RedisRecordHashKey key) {
        if (map.get(key.toString()) == null) {
            return 0;
        }
        return Integer.parseInt((String) map.get(key.toString()));
    }
}

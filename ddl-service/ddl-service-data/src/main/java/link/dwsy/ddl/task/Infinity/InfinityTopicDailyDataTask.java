package link.dwsy.ddl.task.Infinity;

import link.dwsy.ddl.constants.task.RedisInfinityRecordHashKey;
import link.dwsy.ddl.constants.task.RedisRecordKey;
import link.dwsy.ddl.entity.Data.Infinity.InfinityTopicDailyData;
import link.dwsy.ddl.repository.Data.Infinity.InfinityTopicDailyRepository;
import link.dwsy.ddl.service.Impl.Infinity.InfinityRedisRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

@Configuration
@Slf4j
public class InfinityTopicDailyDataTask {

    @Resource
    private InfinityRedisRecordService infinityRedisRecordService;

    @Resource
    private InfinityTopicDailyRepository infinityTopicDailyRepository;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @Scheduled(cron = "0 5 0 * * ? ")
    public void record() {
        log.info("定时任务执行{}", Thread.currentThread().getName());
        Set<ZSetOperations.TypedTuple<String>> typedTuples =
                redisTemplate.opsForZSet().reverseRangeWithScores(RedisRecordKey.RedisInfinityTopicRecordKey, 0, - 1);
        if (typedTuples==null) {
            log.info("今日无访问");
            return;
        }
        ArrayList<InfinityTopicDailyData> dailyDataArrayList = new ArrayList<>(typedTuples.size());
        typedTuples.forEach(p -> {
            String id = p.getValue();
            if (id == null) {
                return;
            }
            Map<Object, Object> dataMap = redisTemplate.opsForHash().entries(RedisRecordKey.RedisInfinityTopicRecordKey + id);
            if (dataMap.isEmpty()) {
                return;
            }
            int view = getMapValue(dataMap, RedisInfinityRecordHashKey.view);
            int quote = getMapValue(dataMap, RedisInfinityRecordHashKey.quote);
            int share = getMapValue(dataMap, RedisInfinityRecordHashKey.share);
            int reply = getMapValue(dataMap, RedisInfinityRecordHashKey.reply);
            int follow = getMapValue(dataMap, RedisInfinityRecordHashKey.follow);
            int score = 0;
            score += view;
            score += quote * 15;
            score -= share * 10;
            score += reply * 4;

            InfinityTopicDailyData infinityTopicDailyData = InfinityTopicDailyData.builder()
                    .InfinityTopicId(Long.parseLong(id))
                    .viewNum(view)
                    .infinityNum(quote)
                    .followerNum(follow)
                    .replyNum(reply)
                    .score(score)
                    .dataDate(LocalDate.now())
                    .build();
            dailyDataArrayList.add(infinityTopicDailyData);
            redisTemplate.delete(RedisRecordKey.RedisInfinityTopicRecordKey + id);
        });
        infinityTopicDailyRepository.saveAll(dailyDataArrayList);
        redisTemplate.delete(RedisRecordKey.RedisInfinityTopicRecordToDayKey);

    }

    private int getMapValue(Map<Object, Object> map, RedisInfinityRecordHashKey key) {
        if (map.get(key.toString()) == null) {
            return 0;
        }
        return Integer.parseInt((String) map.get(key.toString()));
    }
}

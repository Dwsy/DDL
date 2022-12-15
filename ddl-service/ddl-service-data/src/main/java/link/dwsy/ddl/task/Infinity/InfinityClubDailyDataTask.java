package link.dwsy.ddl.task.Infinity;

import link.dwsy.ddl.constants.task.RedisInfinityRecordHashKey;
import link.dwsy.ddl.constants.task.RedisRecordKey;
import link.dwsy.ddl.entity.Data.Infinity.InfinityClubDailyData;
import link.dwsy.ddl.repository.Data.Infinity.InfinityClubDailyDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class InfinityClubDailyDataTask {


    @Resource
    private InfinityClubDailyDataRepository infinityClubDailyDataRepository;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @Scheduled(cron = "0 5 0 * * ? ")
    public void record() {
        log.info("定时任务执行{}", Thread.currentThread().getName());
        Set<String> idSet = redisTemplate.opsForSet().members(RedisRecordKey.RedisInfinityClubRecordKey);

        if (idSet != null) {
            if (idSet.size() == 0) {
                log.info("今日无访问");
                return;
            }
            ArrayList<InfinityClubDailyData> dailyDataArrayList = new ArrayList<>(idSet.size());
            for (String id : idSet) {
                Map<Object, Object> dataMap = redisTemplate.opsForHash().entries(RedisRecordKey.RedisInfinityClubRecordKey + id);
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

                InfinityClubDailyData infinityTopicDailyData = InfinityClubDailyData.builder()
                        .InfinityClubId(Long.parseLong(id))
                        .viewNum(view)
                        .infinityNum(quote)
                        .followerNum(follow)
                        .replyNum(reply)
                        .score(score)
                        .dataDate(LocalDate.now())
                        .build();
                dailyDataArrayList.add(infinityTopicDailyData);
            }
            infinityClubDailyDataRepository.saveAll(dailyDataArrayList);
            for (String id : idSet) {
                redisTemplate.delete(RedisRecordKey.RedisInfinityClubRecordKey + id);
            }
            redisTemplate.delete(RedisRecordKey.RedisInfinityClubRecordKey);
        }

    }

    private int getMapValue(Map<Object, Object> map, RedisInfinityRecordHashKey key) {
        if (map.get(key.toString()) == null) {
            return 0;
        }
        return Integer.parseInt((String) map.get(key.toString()));
    }
}

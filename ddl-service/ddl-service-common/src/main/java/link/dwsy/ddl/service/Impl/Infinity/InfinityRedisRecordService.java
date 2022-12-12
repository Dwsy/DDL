package link.dwsy.ddl.service.Impl.Infinity;

import link.dwsy.ddl.constants.task.RedisInfinityRecordHashKey;
import link.dwsy.ddl.constants.task.RedisRecordKey;
import link.dwsy.ddl.entity.Infinity.Infinity;
import link.dwsy.ddl.entity.Infinity.InfinityTopic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/12/9
 */

@Service
@Slf4j
public class InfinityRedisRecordService {

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @Resource
    private InfinityClubRedisRecordService infinityClubRedisRecordService;
    @Resource
    private InfinityTopicRedisRecordService infinityTopicRedisRecordService;
    public void record(Long id, RedisInfinityRecordHashKey recordHashKey, int increment, Infinity infinity) {
        if (recordHashKey == RedisInfinityRecordHashKey.view) {
            redisTemplate.opsForSet().add(RedisRecordKey.RedisInfinityRecordKey, String.valueOf(id));
        }
        redisTemplate.opsForHash().increment(RedisRecordKey.RedisInfinityRecordKey + id, recordHashKey.toString(), increment);
        redisTemplate.opsForZSet().incrementScore(RedisRecordKey.RedisInfinityRecordToDayKey, String.valueOf(id), getScore(recordHashKey, increment));
        if (infinity != null) {
            if (infinity.getInfinityClub() != null) {
                infinityClubRedisRecordService.record(infinity.getInfinityClub().getId(), recordHashKey, increment);
            }
            List<InfinityTopic> topics = infinity.getInfinityTopics();
            if (!topics.isEmpty()) {
                for (InfinityTopic topic : topics) {
                    infinityTopicRedisRecordService.record(topic.getId(), recordHashKey, increment);
                }
            }
        }
    }

    private int getScore(RedisInfinityRecordHashKey recordHashKey, int increment) {
        int score = 0;
        switch (recordHashKey) {
            case up:
                score = 4;
                break;
            case view:
                score = 1;
                break;
            case comment:
                score = 8;
        }
        return score * increment;
    }
}

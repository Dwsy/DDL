package link.dwsy.ddl.service.Impl.Infinity;

import link.dwsy.ddl.constants.task.RedisInfinityRecordHashKey;
import link.dwsy.ddl.constants.task.RedisRecordKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/12/9
 */

@Service
@Slf4j
public class InfinityClubRedisRecordService {

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;


    public void record(Long id, RedisInfinityRecordHashKey recordHashKey, int increment) {
        if (recordHashKey == RedisInfinityRecordHashKey.view) {
            redisTemplate.opsForSet().add(RedisRecordKey.RedisInfinityClubRecordKey, String.valueOf(id));
        }
        redisTemplate.opsForHash().increment(RedisRecordKey.RedisInfinityClubRecordKey + id, recordHashKey.toString(), increment);
        redisTemplate.opsForZSet().incrementScore(RedisRecordKey.RedisInfinityClubRecordToDayKey, String.valueOf(id), getScore(recordHashKey, increment));
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
            case quote:
                score = 15;
        }
        return score * increment;
    }
}

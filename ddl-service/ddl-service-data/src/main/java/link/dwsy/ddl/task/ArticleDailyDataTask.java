package link.dwsy.ddl.task;

import link.dwsy.ddl.constants.task.RedisRecordHashKey;
import link.dwsy.ddl.constants.task.RedisRecordKey;
import link.dwsy.ddl.entity.Data.Article.ArticleDailyData;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.Data.Article.ArticleDailyDataRepository;
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
public class ArticleDailyDataTask {

    @Resource
    private ArticleFieldRepository articleFieldRepository;

    @Resource
    private ArticleDailyDataRepository articleDailyDataRepository;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @Scheduled(cron = "0 5 0 * * ? ")
    public void record() {
        log.info("定时任务执行{}", Thread.currentThread().getName());
        Set<String> idSet = redisTemplate.opsForSet().members(RedisRecordKey.RedisArticleRecordKey);

        if (idSet != null) {
            if (idSet.size() == 0) {
                log.info("今日无访问");
                return;
            }
            ArrayList<ArticleDailyData> dailyDataArrayList = new ArrayList<>(idSet.size());
            for (String id : idSet) {
                Map<Object, Object> dataMap = redisTemplate.opsForHash().entries(RedisRecordKey.RedisArticleRecordKey + id);
                if (dataMap.isEmpty()) {
                    return;
                }
                int view = getMapValue(dataMap, RedisRecordHashKey.view);
                int up = getMapValue(dataMap, RedisRecordHashKey.up);
                int down = getMapValue(dataMap, RedisRecordHashKey.down);
                int collect = getMapValue(dataMap, RedisRecordHashKey.collect);
                int comment = getMapValue(dataMap, RedisRecordHashKey.comment);
                int score = 0;
                score += view;
                score += up * 2;
                score -= down * 2;
                score += collect * 10;
                score += comment * 5;

                long articleId = Long.parseLong(id);
                ArticleDailyData articleDailyData = ArticleDailyData.builder()
                        .userId(articleFieldRepository.findUserIdById(articleId))
                        .articleFieldId(articleId)
                        .upNum(up)
                        .downNum(down)
                        .commentNum(comment)
                        .viewNum(view)
                        .collectNum(collect)
                        .score(score)
                        .dataDate(LocalDate.now())
                        .tagIds(articleFieldRepository.getTagIdsById(articleId))
                        .groupId(articleFieldRepository.getGroupIdById(articleId))
                        .build();
                dailyDataArrayList.add(articleDailyData);
            }
            articleDailyDataRepository.saveAll(dailyDataArrayList);
            for (String id : idSet) {
                redisTemplate.delete(RedisRecordKey.RedisArticleRecordKey + id);
            }
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

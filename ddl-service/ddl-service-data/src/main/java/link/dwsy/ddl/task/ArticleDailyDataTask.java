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
                Integer view = (Integer) dataMap.get(RedisRecordHashKey.view);
                Integer up = (Integer) dataMap.get(RedisRecordHashKey.up);
                Integer down = (Integer) dataMap.get(RedisRecordHashKey.down);
                Integer collect = (Integer) dataMap.get(RedisRecordHashKey.collect);
                Integer comment = (Integer) dataMap.get(RedisRecordHashKey.comment);
                int score = 0;
                if (view != null) {
                    score += view;
                }
                if (up != null) {
                    score += up*2;
                }
                if (down != null) {
                    score -= down*2;
                }
                if (collect != null) {
                    score += collect*10;
                }
                if (comment != null) {
                    score += comment*5;
                }
                ArticleDailyData articleDailyData = ArticleDailyData.builder()
                        .id(Long.parseLong(id))
                        .articleFieldId(articleFieldRepository.findById(Long.parseLong(id)).get().getId())
                        .upNum(up == null ? 0 : up)
                        .downNum(down == null ? 0 : down)
                        .commentNum(comment == null ? 0 : comment)
                        .viewNum(view == null ? 0 : view)
                        .collectNum(collect == null ? 0 : collect)
                        .score(score)
                        .date(LocalDate.now())
                        .build();
                dailyDataArrayList.add(articleDailyData);
                redisTemplate.delete(RedisRecordKey.RedisArticleRecordKey + id);
            }
            articleDailyDataRepository.saveAll(dailyDataArrayList);
            redisTemplate.delete(RedisRecordKey.RedisArticleRecordKey);
        }

    }
}

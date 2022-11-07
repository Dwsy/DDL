package link.dwsy.ddl.task;

import link.dwsy.ddl.constants.task.RedisRecordKey;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.entity.Data.Article.ArticleDailyData;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.Data.Article.ArticleDailyDataRepository;
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
public class ArticleDailyDataTask {

    @Resource
    private ArticleFieldRepository articleFieldRepository;

    @Resource
    private ArticleDailyDataRepository articleDailyDataRepository;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;


    @Scheduled(cron = "0 5 0 * * ? ")
//    @Scheduled(cron = "0 * * * * ? ")
    public void record() {
        log.info("定时任务执行{}", Thread.currentThread().getName());
        Set<Object> ArticleIdSet = redisTemplate.opsForHash().keys(RedisRecordKey.RedisArticleRecordKey);
        if (ArticleIdSet.size()==0) {
            log.info("今日无文章访问");
            return;
        }
        List<ArticleField> articleFieldList = articleFieldRepository.findAllById
                (ArticleIdSet.stream().map(Object::toString).map(Long::parseLong).collect(Collectors.toList()));
        ArrayList<ArticleDailyData> dailyDataArrayList = new ArrayList<>(articleFieldList.size());
        for (ArticleField articleField : articleFieldList) {
            ArticleDailyData data = ArticleDailyData.builder()
                    .articleFieldId(articleField.getId())
                    .upNum(articleField.getUpNum())
                    .downNum(articleField.getDownNum())
                    .user(articleField.getUser())
                    .title(articleField.getTitle())
                    .commentNum(articleField.getCommentNum())
                    .viewNum(articleField.getViewNum())
                    .summary(articleField.getSummary())
                    .banner(articleField.getBanner())
                    .collectNum(articleField.getCollectNum())
                    .build();
            dailyDataArrayList.add(data);
        }
        articleDailyDataRepository.saveAll(dailyDataArrayList);
        redisTemplate.delete(RedisRecordKey.RedisArticleRecordKey);
    }
}

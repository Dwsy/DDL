package link.dwsy.ddl.config;

import link.dwsy.ddl.service.Impl.ArticleRedisRecordService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/12/9
 */
@Component
public class DataApplicationRunner implements ApplicationRunner {

    @Resource
    private ArticleRedisRecordService articleRedisRecordService;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;
    @Override
    public void run(ApplicationArguments args) throws Exception {
//        if (redisTemplate.hasKey(RedisRecordKey.RedisArticleRecordKey)) {
//
//        }
    }


}


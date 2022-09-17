package link.dwsy.ddl;

import cn.hutool.core.util.RandomUtil;
import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.entity.User.UserActive;
import link.dwsy.ddl.repository.User.UserActiveRepository;
import link.dwsy.ddl.service.Impl.UserActiveServiceImpl;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class userTest {



    @Resource
    UserActiveRepository userActiveRepository;
    @Resource
    UserSupport userSupport;

    @Resource
    UserActiveServiceImpl userActiveService;

    @Resource
    RedisTemplate<String,String> redisTemplate;

    @Test
    public void T() {
        checkIn();
        checkIn();
        checkIn();
    }

    public void checkIn(){
        Date tomorrowZero = DateUtil.getTomorrowZero();
        Date zero = DateUtil.getZero();
        var uid = 5L;
        if (redisTemplate.opsForValue().get("checkIn:" + uid) != null) {
            System.out.println("今日已签到redis");
        }
        if (userActiveRepository.existsByUserIdIsAndUserActiveTypeAndCreateTimeBetween
                (uid, UserActiveType.Check_In, zero,tomorrowZero)) {
            System.out.println("今日已签到");
            return;
        }


        userActiveRepository.save(UserActive.builder()
                .userActiveType(UserActiveType.Check_In).userId(uid)
                .sourceId(null).build());
        redisTemplate.opsForValue().set("checkIn:" + uid, "true"
                ,DateUtil.getRemainSecondsOneDay()+ RandomUtil.randomInt(-10,10), TimeUnit.SECONDS);
        System.out.println("签到成功");
    }



}

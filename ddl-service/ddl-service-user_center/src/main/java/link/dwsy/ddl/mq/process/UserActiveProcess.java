package link.dwsy.ddl.mq.process;

import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.entity.User.UserActive;
import link.dwsy.ddl.repository.User.UserActiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Author Dwsy
 * @Date 2022/9/8
 */
@Service
@Slf4j
public class UserActiveProcess {

    @Resource
    private UserActiveRepository userActiveRepository;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    public void ActiveLog(UserActiveType userActiveType, Long sourceId, Long uid, String ua) {
        //不重复记录
        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent
                ("userActive:" + uid + ":" + userActiveType.toString() + sourceId, "1", 15, TimeUnit.MINUTES);
        if (Boolean.TRUE.equals(ifAbsent)) {
            UserActive userActive = userActiveRepository.
                    findByDeletedFalseAndUserIdAndUserActiveTypeAndSourceId(uid, userActiveType, sourceId);
            if (userActive != null) {
                userActive.setUa(ua);
                userActive.setCreateTime(new Date());
                userActive.setLastModifiedTime(new Date());
                userActiveRepository.save(userActive);
            } else {
                log.info("用户活跃记录：userActiveType{}sourceId {} uid{} ua{}", userActiveType, sourceId, uid, ua);
                userActiveRepository.save(UserActive.builder()
                        .userActiveType(userActiveType).userId(uid)
                        .sourceId(sourceId).ua(ua).build());
            }

        } else {
            log.info("用户活跃记录最近已记录：userActiveType{}sourceId {} uid{} ua{}", userActiveType, sourceId, uid, ua);
        }

    }
}

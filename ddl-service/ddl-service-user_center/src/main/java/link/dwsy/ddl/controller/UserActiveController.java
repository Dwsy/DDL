package link.dwsy.ddl.controller;

import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.repository.User.UserActiveRepository;
import link.dwsy.ddl.service.Impl.UserActiveServiceImpl;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.DateUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Author Dwsy
 * @Date 2022/9/5
 */
@RestController
@RequestMapping("/active")
public class UserActiveController {

    @Resource
    UserActiveRepository userActiveRepository;
    @Resource
    UserSupport userSupport;

    @Resource
    UserActiveServiceImpl userActiveService;

    @Resource
    RedisTemplate<String,String> redisTemplate;

    @PostMapping("/check")
    @AuthAnnotation(Level = 0)
    public String checkIn() {
        Date tomorrowZero = DateUtil.getTomorrowZero();
        Date zero = DateUtil.getZero();
        Long id = userSupport.getCurrentUser().getId();
        if (redisTemplate.opsForValue().get("checkIn:" + id) != null) {
            throw  new CodeException("今日已签到");

        }
        if (userActiveRepository.existsByUserIdIsAndUserActiveTypeAndCreateTimeBetween
                (id, UserActiveType.Check_In, zero, tomorrowZero)) {
            throw  new CodeException("今日已签到");
        }
        userActiveService.ActiveLog(UserActiveType.Check_In, null);
        redisTemplate.opsForValue().set("checkIn:" + id, "true",DateUtil.getRemainSecondsOneDay(), TimeUnit.SECONDS);
        return "签到成功";
    }


}

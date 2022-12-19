package link.dwsy.ddl.aspect;


import link.dwsy.ddl.annotation.Points;
import link.dwsy.ddl.service.Impl.PointsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Aspect
@Slf4j
public class PointsAspect {


//    @Resource
//    private UserSupport userSupport;
//
//    @Resource
//    private RabbitTemplate rabbitTemplate;
//
//    @Resource(name = "stringRedisTemplate")
//    private StringRedisTemplate redisTemplate;

    @Resource
    private PointsServiceImpl pointsService;

    @Pointcut("@annotation(link.dwsy.ddl.annotation.Points)")
    public void cut() {
    }

    @After("cut() && @annotation(points)")
    public void dop(Points points) {
        pointsService.award(points);
    }
}

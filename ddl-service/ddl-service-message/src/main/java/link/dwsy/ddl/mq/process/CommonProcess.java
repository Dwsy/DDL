package link.dwsy.ddl.mq.process;

import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.entity.User.UserActive;
import link.dwsy.ddl.repository.User.UserActiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/12/6
 */

@Service
@Slf4j
public class CommonProcess {
    @Resource
    private UserActiveRepository userActiveRepository;

    public void ActiveLog(UserActiveType userActiveType, Long sourceId, Long uid, String ua) {
        //todo 15分钟内不重复记录
        log.info("用户活跃记录：userActiveType{}sourceId {} uid{} ua{}", userActiveType, sourceId, uid, ua);
        userActiveRepository.save(UserActive.builder()
                .userActiveType(userActiveType).userId(uid)
                .sourceId(sourceId).ua(ua).build());
    }
}

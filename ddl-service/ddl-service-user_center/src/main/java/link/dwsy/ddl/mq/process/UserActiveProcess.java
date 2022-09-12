package link.dwsy.ddl.mq.process;

import link.dwsy.ddl.XO.Enum.UserActiveType;
import link.dwsy.ddl.entity.User.UserActive;
import link.dwsy.ddl.repository.User.UserActiveRepository;
import link.dwsy.ddl.support.UserSupport;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/9/8
 */
@Service
public class UserActiveProcess {
    @Resource
    UserSupport userSupport;
    @Resource
    UserActiveRepository userActiveRepository;

    public void ActiveLog(UserActiveType userActiveType, Long sourceId, Long uid, String ua) {
        userActiveRepository.save(UserActive.builder()
                .userActiveType(userActiveType).userId(uid)
                .sourceId(sourceId).ua(ua).build());
    }
}

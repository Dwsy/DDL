package link.dwsy.ddl.mq.process;

import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.entity.User.UserActive;
import link.dwsy.ddl.repository.User.UserActiveRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/9/8
 */
@Service
public class UserActiveProcess {

    @Resource
    UserActiveRepository userActiveRepository;

    public void ActiveLog(UserActiveType userActiveType, Long sourceId, Long uid, String ua) {
        //todo 15分钟内不重复记录
        userActiveRepository.save(UserActive.builder()
                .userActiveType(userActiveType).userId(uid)
                .sourceId(sourceId).ua(ua).build());
    }
}

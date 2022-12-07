package link.dwsy.ddl.service.Impl;

import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.XO.Message.UserActiveMessage;
import link.dwsy.ddl.constants.mq.UserActiveMQConstants;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.entity.User.UserActive;
import link.dwsy.ddl.repository.User.UserActiveRepository;
import link.dwsy.ddl.service.UserActiveService;
import link.dwsy.ddl.support.UserSupport;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/9/5
 */

@Service
public class UserActiveServiceImpl implements UserActiveService {

    @Resource
    UserSupport userSupport;
    @Resource
    UserActiveRepository userActiveRepository;

    @Resource
    RabbitTemplate rabbitTemplate;


    public void ActiveLog(UserActiveType userActiveType, Long sourceId) {
        LoginUserInfo currentUser = userSupport.getCurrentUser();
        if (currentUser != null) {
            userActiveRepository.save(UserActive.builder()
                    .userActiveType(userActiveType).userId(userSupport.getCurrentUser().getId())
                    .sourceId(sourceId).build());
        }
    }

    //            rabbitTemplate.convertAndSend(UserActiveMQConstants.QUEUE_DDL_USER_ACTIVE, userActiveMessage);
    public void ActiveLogUseMQ(UserActiveType userActiveType, Long sourceId) {
        LoginUserInfo currentUser = userSupport.getCurrentUser();
        if (currentUser != null) {
            UserActiveMessage userActive = UserActiveMessage.builder()
                    .userActiveType(userActiveType).userId(userSupport.getCurrentUser().getId())
                    .sourceId(sourceId).build();
            rabbitTemplate.convertAndSend(UserActiveMQConstants.QUEUE_DDL_USER_ACTIVE, userActive);
        }
    }
}


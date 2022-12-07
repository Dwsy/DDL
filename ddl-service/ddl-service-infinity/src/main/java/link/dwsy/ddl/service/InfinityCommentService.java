package link.dwsy.ddl.service;

import com.alibaba.fastjson2.JSON;
import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.XO.Message.UserInfinityNotifyMessage;
import link.dwsy.ddl.constants.mq.UserActiveMQConstants;
import link.dwsy.ddl.entity.Infinity.Infinity;
import link.dwsy.ddl.repository.Infinity.InfinityRepository;
import link.dwsy.ddl.support.UserSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/12/6
 */
@Service
@Slf4j
public class InfinityCommentService {

    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    UserSupport userSupport;

    @Resource
    InfinityRepository infinityRepository;

    public void sendActionMqMessage(long userId, Infinity infinity, UserActiveType userActiveType, long toId, boolean cancel) {
        Long toUserId = infinityRepository.getUserIdById(toId);
        String toContent = infinityRepository.getContentById(toId);
        UserInfinityNotifyMessage activeMessage = UserInfinityNotifyMessage.builder()
                .userActiveType(userActiveType)
                .toUserId(toUserId)
                .formUserId(userId)
                .infinityId(infinity.getId())
                .parentTweetId(infinity.getParentTweetId())
                .parentUserId(infinity.getParentUserId())
                .replyUserTweetId(infinity.getReplyUserTweetId())
                .refId(infinity.getRefId())
                .ua(userSupport.getUserAgent())
                .formContent(infinity.getContent())
                .toContent(toContent)
                .cancel(cancel)
                .build();
        log.info("sendActionMqMessage{}", JSON.toJSONString(activeMessage));
        rabbitTemplate.convertAndSend(UserActiveMQConstants.QUEUE_DDL_USER_INFINITY_ACTIVE, activeMessage);
    }
}

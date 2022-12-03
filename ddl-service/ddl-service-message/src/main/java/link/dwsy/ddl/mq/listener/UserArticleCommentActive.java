package link.dwsy.ddl.mq.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import link.dwsy.ddl.XO.Message.UserCommentNotifyMessage;
import link.dwsy.ddl.constants.mq.UserActiveMQConstants;
import link.dwsy.ddl.mq.process.UserArticleCommentActiveProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/9/8
 */
@Component
@Slf4j
public class UserArticleCommentActive {
    @Resource
    UserArticleCommentActiveProcess userCommentActiveProcess;

    @RabbitListener(queues = UserActiveMQConstants.QUEUE_DDL_USER_ARTICLE_COMMENT_ACTIVE)
    public void sendQaNotify(UserCommentNotifyMessage message) throws JsonProcessingException {

//        System.out.println(JSON.toJSONString(message));
        userCommentActiveProcess.sendNotify(message);

//        if (message.getUserActiveType() == null) {
//            System.out.println(message.getCommentId());
//            return;
//
//        }
//
    }


}

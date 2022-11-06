package link.dwsy.ddl.mq.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import link.dwsy.ddl.XO.Message.UserQuestionAnswerNotifyMessage;
import link.dwsy.ddl.constants.mq.UserActiveConstants;
import link.dwsy.ddl.mq.process.UserQuestionAnswerActiveProcess;
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
public class UserQuestionAnswerActive {
    @Resource
    private UserQuestionAnswerActiveProcess userQuestionAnswerActiveProcess;

    @RabbitListener(queues = UserActiveConstants.QUEUE_DDL_USER_QUESTION_ANSWER_OR_COMMENT_ACTIVE)
    public void sendNotify(UserQuestionAnswerNotifyMessage message) throws JsonProcessingException {

//        System.out.println(JSON.toJSONString(message));
        userQuestionAnswerActiveProcess.sendNotify(message);

//        if (message.getUserActiveType() == null) {
//            System.out.println(message.getCommentId());
//            return;
//
//        }
//
    }


}

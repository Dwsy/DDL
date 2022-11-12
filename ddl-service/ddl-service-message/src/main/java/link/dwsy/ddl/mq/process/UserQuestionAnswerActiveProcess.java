package link.dwsy.ddl.mq.process;

import com.fasterxml.jackson.core.JsonProcessingException;
import link.dwsy.ddl.XO.Enum.Message.NotifyType;
import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.XO.Message.Question.InvitationUserAnswerQuestionMsg;
import link.dwsy.ddl.XO.Message.UserQuestionAnswerNotifyMessage;
import link.dwsy.ddl.XO.VO.Notify.CommentNotifyVO;
import link.dwsy.ddl.entity.User.UserNotify;
import link.dwsy.ddl.repository.QA.QaAnswerRepository;
import link.dwsy.ddl.repository.QA.QaQuestionFieldRepository;
import link.dwsy.ddl.repository.User.UserNotifyRepository;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.HtmlHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Dwsy
 * @Date 2022/9/8
 */
@Service
@Slf4j
public class UserQuestionAnswerActiveProcess {
    @Resource
    private UserSupport userSupport;

    @Resource
    private QaAnswerRepository qaAnswerRepository;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private QaQuestionFieldRepository qaQuestionFieldRepository;

    @Resource
    private UserNotifyRepository userNotifyRepository;


    public void sendNotify(UserQuestionAnswerNotifyMessage message) throws JsonProcessingException {
        if (message.isCancel()) {
            //todo 取消操作
//            redisTemplate.opsForSet().remove(message.getUserActiveType().toString(), message.getUserId().toString());
            return;
        }
        Long formUserId = message.getFormUserId();
        Long questionId = message.getQuestionId();
        Long answerId = message.getAnswerId();
        String toContent = message.getToContent();
        String formContent = message.getFormContent();
        UserActiveType userActiveType = message.getUserActiveType();
        CommentNotifyVO notify = null;
        UserNotify userNotify;
        Long toUserId;
        boolean sendNotify;
        log.info(userActiveType.toString());
        switch (userActiveType) {
            case Answer_Question:
                toUserId = qaQuestionFieldRepository.getUserIdByQuestionId(questionId);
                if (toUserId != null) {
                    userNotify = UserNotify.builder()
                            .fromUserId(formUserId)
                            .toUserId(toUserId)
                            .questionId(questionId)
                            .answerId(answerId)
//                            .commentId(commentId)
                            .notifyType(NotifyType.answer)
                            .formContent(HtmlHelper.toPure(formContent))
                            .toContent(toContent)
                            .replayAnswerId(message.getReplayAnswerId())
                            .build();
                    if (!toUserId.equals(formUserId)) {
                        sendNotify = true;
                    } else {
                        sendNotify = false;
                    }
                } else {
                    log.error("用户{}回答问题{}失败", formUserId, questionId);
                    return;
                }
                break;
            case Question_Comment:
                toUserId = qaQuestionFieldRepository.getUserIdByQuestionId(questionId);
                if (toUserId != null) {
                    userNotify = UserNotify.builder()
                            .fromUserId(formUserId)
                            .toUserId(toUserId)
                            .questionId(questionId)
                            .answerId(answerId)
                            .notifyType(NotifyType.question_comment)
                            .formContent(formContent)
                            .toContent(toContent)
                            .replayAnswerId(message.getReplayAnswerId())
                            .build();
                    sendNotify = !toUserId.equals(formUserId);
                } else {
                    log.error("用户{}回复问题{}失败", formUserId, questionId);
                    return;
                }
                break;
            case Answer_Comment:
                toUserId = qaQuestionFieldRepository.getUserIdByQuestionId(questionId);
                if (toUserId != null) {
                    userNotify = UserNotify.builder()
                            .fromUserId(formUserId)
                            .toUserId(toUserId)
                            .questionId(questionId)
                            .answerId(answerId)
//                            .commentId(commentId)
                            .notifyType(NotifyType.answer_comment)
                            .formContent(formContent)
                            .toContent(HtmlHelper.toPure(toContent))
                            .replayAnswerId(message.getReplayAnswerId())
                            .build();
//                    notify = new CommentNotifyVO(userNotify);
                    sendNotify = !toUserId.equals(formUserId);
                } else {
                    log.error("用户{}回复回答{}失败", formUserId, questionId);
                    return;
                }
                break;
            case UP_Question:
                toUserId = qaQuestionFieldRepository.getUserIdByQuestionId(questionId);
                if (userNotifyRepository.existsByDeletedFalseAndFromUserIdAndToUserIdAndAnswerIdAndNotifyTypeAndQuestionId(
                        formUserId, toUserId, answerId, NotifyType.up_question, questionId)) {
                    log.info("用户{}已经通知过用户{}了", formUserId, toUserId);
                    return;
                }
                sendNotify = !toUserId.equals(formUserId);
                if (sendNotify) {
                    toContent = qaQuestionFieldRepository.getTitle(questionId);
                    userNotify = UserNotify.builder()
                            .fromUserId(formUserId)
                            .toUserId(toUserId)
                            .questionId(questionId)
                            .answerId(answerId)
                            .notifyType(NotifyType.up_question)
                            .toContent(toContent)
                            .build();
//                    notify = new CommentNotifyVO(userNotify);
                } else {
                    return;
                }
                break;
            case UP_Question_Answer:
                toUserId = qaAnswerRepository.getUserIdByAnswerId(answerId);
                if (userNotifyRepository.existsByDeletedFalseAndFromUserIdAndToUserIdAndAnswerIdAndNotifyTypeAndQuestionId(
                        formUserId, toUserId, answerId, NotifyType.up_question_answer, questionId)) {
                    log.info("用户{}已经通知过用户{}了", formUserId, toUserId);
                    return;
                }
                sendNotify = !toUserId.equals(formUserId);
                if (sendNotify) {
                    String toContent_pureText = HtmlHelper.toPure(qaAnswerRepository.getHtmlText(answerId));
                    userNotify = UserNotify.builder()
                            .fromUserId(formUserId)
                            .toUserId(toUserId)
                            .questionId(questionId)
                            .answerId(answerId)
                            .notifyType(NotifyType.up_question_answer)
                            .formContent(formContent)
                            .toContent(toContent_pureText)
                            .build();
//                    notify = new CommentNotifyVO(userNotify);
                } else {
                    return;
                }
                break;
            case Accepted_Question_Answer:
                toUserId = qaAnswerRepository.getUserIdByAnswerId(answerId);
                if (userNotifyRepository.existsByDeletedFalseAndFromUserIdAndToUserIdAndAnswerIdAndNotifyTypeAndQuestionId(
                        formUserId, toUserId, answerId, NotifyType.accepted_question_answer, questionId)) {
                    log.info("用户{}已经通知过用户{}了", formUserId, toUserId);
                    return;
                }
                sendNotify = !toUserId.equals(formUserId);
                if (sendNotify) {
                    String toContent_pureText = HtmlHelper.toPure(qaAnswerRepository.getHtmlText(answerId));
                    userNotify = UserNotify.builder()
                            .fromUserId(formUserId)
                            .toUserId(toUserId)
                            .questionId(questionId)
                            .answerId(answerId)
                            .notifyType(NotifyType.accepted_question_answer)
                            .formContent(qaQuestionFieldRepository.getTitle(questionId))
                            .toContent(toContent_pureText)
                            .build();
                } else {
                    return;
                }
                break;
            //todo at cancel redis stack json
            default:
                log.info("Unexpected value: " + userActiveType);
                return;
        }


        if (sendNotify) {
            userNotifyRepository.save(userNotify);
//            String json = objectMapper.writeValueAsString(notify);
//            String key = UserNotifyConstants.QUEUE_DDL_USER_ARTICLE_COMMENT_NOTIFY_REDIS_KEY_PREFIX + toUserId + ":" + userActiveType;
//            redisTemplate.opsForValue().increment(key, 1);
//            redisTemplate.opsForList().leftPush(key, json);
            log.info("用户{}qa:{}通知{}成功", message.getFormUserId(), message.getUserActiveType().toString(), userNotify.getToUserId());
        }


//        redisTemplate.opsForSet().add("Article:Comment:Notify:UID:" + toUserId, notifyJson);

    }

    public void sendInvitationNotify(InvitationUserAnswerQuestionMsg message) {
        long formUserId = message.getFormUserId();
        long toUserId = message.getToUserId();
        long questionId = message.getQuestionId();
        String answerTitle = message.getAnswerTitle();
        UserNotify userNotify = UserNotify.builder()
                .fromUserId(formUserId)
                .toUserId(toUserId)
                .questionId(questionId)
                .notifyType(NotifyType.invitation_user_answer_question)
                .formContent(answerTitle)
                .cancel(false)
                .build();
        userNotifyRepository.save(userNotify);
        log.info("用户:{}邀请:{}回答:{}/{}成功", formUserId, toUserId, answerTitle, questionId);
    }
}

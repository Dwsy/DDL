package link.dwsy.ddl.mq.process;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import link.dwsy.ddl.XO.Enum.Message.NotifyType;
import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.XO.Message.UserInfinityNotifyMessage;
import link.dwsy.ddl.entity.User.UserNotify;
import link.dwsy.ddl.repository.User.UserNotifyRepository;
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
public class UserInfinityActiveProcess {

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private UserNotifyRepository userNotifyRepository;


    @Resource
    private CommonProcess commonProcess;


    public void sendNotify(UserInfinityNotifyMessage message) throws JsonProcessingException {
        if (message.isCancel()) {
            //todo 取消操作
            log.info(" UserInfinityActiveProcess 取消操作");
//            redisTemplate.opsForSet().remove(message.getUserActiveType().toString(), message.getUserId().toString());
            return;
        }
        log.info("UserInfinityActiveProcess");
        log.info(JSON.toJSONString(message));
        log.info("UserInfinityActiveProcess");
        Long formUserId = message.getFormUserId();
        Long toUserId = message.getToUserId();
        String ua = message.getUa();
        Long infinityId = message.getInfinityId();
        String formContent = message.getFormContent();
        String toContent = message.getToContent();
        UserActiveType userActiveType = message.getUserActiveType();
        UserNotify userNotify = null;
        boolean sendNotify;
        switch (userActiveType) {
            case Thumb_Tweet:
                if (userNotifyRepository.existsByDeletedFalseAndFromUserIdAndToUserIdAndInfinityIdAndNotifyType(
                        formUserId, toUserId, message.getParentTweetId(), NotifyType.thumbTweet)) {
                    log.info("用户{}已经通知过用户{}了", formUserId, toUserId);
                    return;
                }
                userNotify = UserNotify.builder()
                        .fromUserId(formUserId)
                        .toUserId(toUserId)
                        .infinityId(message.getParentTweetId())
                        .notifyType(NotifyType.thumbTweet)
                        .build();
                commonProcess.ActiveLog(userActiveType, message.getParentTweetId(), formUserId, ua);
                break;
            case Comment_Tweet:
                userNotify = UserNotify.builder()
                        .fromUserId(formUserId)
                        .toUserId(toUserId)
                        .infinityId(message.getParentTweetId())
                        .replayInfinityId(infinityId)
                        .formContent(formContent)
                        .toContent(toContent)
                        .notifyType(NotifyType.comment_tweet)
                        .build();
                break;
//                .parentTweetId(infinity.getParentTweetId())
//                    .parentUserId(infinity.getParentUserId())
//                    .replyUserTweetId(infinity.getReplyUserTweetId())
//                    .refId(infinity.getRefId())
            case Reply_Comment_Tweet:
                userNotify = UserNotify.builder()
                        .fromUserId(formUserId)
                        .toUserId(toUserId)
                        .infinityId(message.getReplyUserTweetId())
                        .replayInfinityId(infinityId)
                        .formContent(formContent)
                        .toContent(toContent)
                        .notifyType(NotifyType.reply_comment_tweet)
                        .build();
                break;
            case Reply_Reply_Comment_Tweet:
                userNotify = UserNotify.builder()
                        .fromUserId(formUserId)
                        .toUserId(toUserId)
                        .infinityId(message.getRefId())
                        .replayInfinityId(infinityId)
                        .formContent(formContent)
                        .toContent(toContent)
                        .notifyType(NotifyType.reply_reply_comment_tweet)
                        .build();
                break;
        }
        if (userNotify != null) {
            userNotifyRepository.save(userNotify);
        }
    }
}

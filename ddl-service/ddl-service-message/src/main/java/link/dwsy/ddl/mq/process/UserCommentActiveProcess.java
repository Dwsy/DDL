package link.dwsy.ddl.mq.process;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import link.dwsy.ddl.XO.Enum.Message.NotifyType;
import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.XO.Message.UserCommentNotifyMessage;
import link.dwsy.ddl.XO.Projection.ArticleFieldInfo;
import link.dwsy.ddl.XO.VO.Notify.CommentNotifyVO;
import link.dwsy.ddl.entity.User.UserNotify;
import link.dwsy.ddl.mq.UserNotifyConstants;
import link.dwsy.ddl.repository.Article.ArticleCommentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.User.UserActiveRepository;
import link.dwsy.ddl.repository.User.UserNotifyRepository;
import link.dwsy.ddl.support.UserSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @Author Dwsy
 * @Date 2022/9/8
 */
@Service
@Slf4j
public class UserCommentActiveProcess {
    @Resource
    UserSupport userSupport;
    @Resource
    UserActiveRepository userActiveRepository;
    @Resource
    ArticleCommentRepository articleCommentRepository;
    @Resource
    RedisTemplate<String, String> redisTemplate;
    @Resource
    ArticleFieldRepository articleFieldRepository;

    @Resource
    UserNotifyRepository userNotifyRepository;

    @Resource
    private ObjectMapper objectMapper;


    public void sendNotify(UserCommentNotifyMessage message) throws JsonProcessingException {
        if (message.isCancel()) {
            //todo 取消操作
//            redisTemplate.opsForSet().remove(message.getUserActiveType().toString(), message.getUserId().toString());
            return;
        }
        Long formUserId = message.getFormUserId();
        Long articleId = message.getArticleId();
        Long commentId = message.getCommentId();
        String toContent = message.getToContent();
        String formContent = message.getFormContent();
        UserActiveType userActiveType = message.getUserActiveType();
        CommentNotifyVO notify = null;
        UserNotify userNotify = null;
        Long toUserId;
        boolean sendNotify = false;
        switch (userActiveType) {
            case Comment_Article:
                toUserId = articleFieldRepository.getUserIdByArticleId(articleId);
                if (toUserId != null) {
                    userNotify = UserNotify.builder()
                            .fromUserId(formUserId)
                            .toUserId(toUserId)
                            .articleId(articleId)
                            .commentId(commentId)
                            .notifyType(NotifyType.comment_article)
                            .formContent(formContent)
                            .toContent(toContent)
                            .replayCommentId(message.getReplayCommentId())
                            .build();
//                    notify = new CommentNotifyVO(userNotify);
                    sendNotify = !toUserId.equals(formUserId);
                } else {
                    log.error("用户{}评论文章{}失败", formUserId, articleId);
                    return;
                }
                break;
            case Comment_Article_Comment:
                toUserId = articleCommentRepository.getUserIdByCommentId(commentId);
                if (toUserId != null) {
                    userNotify = UserNotify.builder()
                            .fromUserId(formUserId)
                            .toUserId(toUserId)
                            .articleId(articleId)
                            .commentId(commentId)
                            .notifyType(NotifyType.comment_article_comment)
                            .formContent(formContent)
                            .toContent(toContent)
                            .replayCommentId(message.getReplayCommentId())
                            .build();
//                    notify = new CommentNotifyVO(userNotify);
                    sendNotify = !toUserId.equals(formUserId);
                } else {
                    log.error("用户{}评论文章评论{}失败", formUserId, commentId);
                    return;
                }
                break;
            case UP_Article:
                toUserId = articleFieldRepository.getUserIdByArticleId(articleId);
                if (userNotifyRepository.existsByDeletedFalseAndFromUserIdAndToUserIdAndCommentIdAndNotifyTypeAndArticleId(
                        formUserId, toUserId, commentId, NotifyType.up_article, articleId)) {
                    log.info("用户{}已经通知过用户{}了", formUserId, toUserId);
                    return;
                }
                sendNotify = !toUserId.equals(formUserId);
                if (sendNotify) {
                    Optional<ArticleFieldInfo> title = articleFieldRepository.getTitle(articleId);
                    if (title.isPresent()) {
                        toContent = title.get().getTitle();
                    }
                    userNotify = UserNotify.builder()
                            .fromUserId(formUserId)
                            .toUserId(toUserId)
                            .articleId(articleId)
                            .commentId(commentId)
                            .notifyType(NotifyType.up_article)
                            .toContent(toContent)
                            .build();
//                    notify = new CommentNotifyVO(userNotify);
                } else {
                    return;
                }
                break;
                // to do 二级评论跳转 直接跳转好像遍历次数有点多 二级评论跳转 先详细在文章？
            case UP_Article_Comment:
                toUserId = articleCommentRepository.getUserIdByCommentId(commentId);
                if (userNotifyRepository.existsByDeletedFalseAndFromUserIdAndToUserIdAndCommentIdAndNotifyType(
                        formUserId, toUserId, commentId, NotifyType.up_article_comment)) {
                    log.info("用户{}已经通知过用户{}了", formUserId, toUserId);
                    return;
                }
                sendNotify = !toUserId.equals(formUserId);
                if (sendNotify) {
                    toContent= String.valueOf(articleCommentRepository.getText(commentId));
                    userNotify = UserNotify.builder()
                            .fromUserId(formUserId)
                            .toUserId(toUserId)
                            .articleId(articleId)
                            .commentId(commentId)
                            .notifyType(NotifyType.up_article_comment)
                            .formContent(formContent)
                            .toContent(toContent)
                            .build();
//                    notify = new CommentNotifyVO(userNotify);
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
            String key = UserNotifyConstants.QUEUE_DDL_USER_ARTICLE_COMMENT_NOTIFY_REDIS_KEY_PREFIX + toUserId + ":" + userActiveType;
            redisTemplate.opsForValue().increment(key, 1);
//            redisTemplate.opsForList().leftPush(key, json);
            log.info("用户{}comment:{}通知{}成功", message.getFormUserId(), message.getUserActiveType().toString(), userNotify.getToUserId());
        }


//        redisTemplate.opsForSet().add("Article:Comment:Notify:UID:" + toUserId, notifyJson);

    }
}

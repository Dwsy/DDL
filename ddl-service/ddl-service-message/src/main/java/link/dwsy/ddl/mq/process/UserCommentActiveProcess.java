package link.dwsy.ddl.mq.process;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import link.dwsy.ddl.XO.Enum.NotifyType;
import link.dwsy.ddl.XO.Enum.UserActiveType;
import link.dwsy.ddl.XO.Message.UserCommentNotifyMessage;
import link.dwsy.ddl.XO.VO.Notify.CommentNotifyVO;
import link.dwsy.ddl.entity.User.UserNotify;
import link.dwsy.ddl.repository.Article.ArticleCommentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.User.UserActiveRepository;
import link.dwsy.ddl.repository.User.UserNotifyRepository;
import link.dwsy.ddl.support.UserSupport;
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

    public void sendNotify(UserCommentNotifyMessage message) throws JsonProcessingException {
        if (message.isCancel()) {
//            redisTemplate.opsForSet().remove(message.getUserActiveType().toString(), message.getUserId().toString());
            return;
        }
        Long formUserId = message.getFormUserId();
        Long articleId = message.getArticleId();
        Long commentId = message.getCommentId();
        String toContent = message.getToContent();
        String formContent = message.getFormContent();
        UserActiveType userActiveType = message.getUserActiveType();
        CommentNotifyVO notify;
        UserNotify userNotify;
        Long toUserId;
        switch (userActiveType) {
            case Comment_Article:
                toUserId = articleFieldRepository.getUserIdByArticleId(articleId);
                userNotify = UserNotify.builder()
                        .fromUserId(formUserId)
                        .toUserId(toUserId)
                        .articleId(articleId)
                        .commentId(commentId)
                        .notifyType(NotifyType.comment)
                        .formContent(formContent)
                        .toContent(toContent)
                        .build();
                notify = new CommentNotifyVO(userNotify);
                break;
            case Comment_Article_Comment:
                toUserId = articleCommentRepository.getUserIdByCommentId(commentId);
                userNotify = UserNotify.builder()
                        .fromUserId(formUserId)
                        .toUserId(toUserId)
                        .articleId(articleId)
                        .commentId(commentId)
                        .notifyType(NotifyType.comment_comment)
                        .formContent(formContent)
                        .toContent(toContent)
                        .build();
                notify = new CommentNotifyVO(userNotify);

                break;
            case UP_Article:
                toUserId = articleFieldRepository.getUserIdByArticleId(articleId);
                userNotify = UserNotify.builder()
                        .fromUserId(formUserId)
                        .toUserId(toUserId)
                        .articleId(articleId)
                        .commentId(commentId)
                        .notifyType(NotifyType.up_article)
                        .formContent(formContent)
                        .toContent(toContent)
                        .build();
                notify = new CommentNotifyVO(userNotify);

                break;
            case UP_Article_Comment:
                toUserId = articleCommentRepository.getUserIdByCommentId(commentId);
                userNotify = UserNotify.builder()
                        .fromUserId(formUserId)
                        .toUserId(toUserId)
                        .articleId(articleId)
                        .commentId(commentId)
                        .notifyType(NotifyType.up_article_comment)
                        .formContent(formContent)
                        .toContent(toContent)
                        .build();
                notify = new CommentNotifyVO(userNotify);

                break;
            //todo at cancel redis stack json
            default:
                log.info("Unexpected value: " + userActiveType);
                return;
        }

        userNotifyRepository.save(userNotify);
        ObjectMapper objectMapper = new ObjectMapper();
        String notifyJson = objectMapper.writeValueAsString(notify);
        redisTemplate.opsForSet().add("Article:Comment:Notify:UID:" + toUserId, notifyJson);

    }
}

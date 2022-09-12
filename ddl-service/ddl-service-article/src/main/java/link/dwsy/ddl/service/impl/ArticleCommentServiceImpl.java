package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.XO.Enum.Article.CommentType;
import link.dwsy.ddl.XO.Enum.UserActiveType;
import link.dwsy.ddl.XO.Message.UserCommentNotifyMessage;
import link.dwsy.ddl.XO.Projection.ArticleFieldInfo;
import link.dwsy.ddl.XO.RB.ArticleCommentActionRB;
import link.dwsy.ddl.XO.RB.ArticleCommentRB;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.Article.ArticleComment;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.mq.UserActiveConstants;
import link.dwsy.ddl.repository.Article.ArticleCommentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PageData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @Author Dwsy
 * @Date 2022/8/26
 */

@Service
public class ArticleCommentServiceImpl {
    @Resource
    private ArticleCommentRepository articleCommentRepository;
    @Resource
    private UserSupport userSupport;
    @Resource
    private ArticleFieldRepository articleFieldRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private RabbitTemplate rabbitTemplate;

    public PageData<ArticleComment> getByArticleId(long aid, PageRequest pageRequest) {
        Page<ArticleComment> parentComment = articleCommentRepository.
                findAllByDeletedIsFalseAndArticleFieldIdAndParentCommentId(aid, 0L, pageRequest);
        for (ArticleComment ArticleComment : parentComment) {
            long pid = ArticleComment.getId();
            ArticleComment.setChildComments(articleCommentRepository.findAllByDeletedIsFalseAndArticleFieldIdAndParentCommentId(aid, pid));
        }
        return new PageData<>(parentComment);
    }

    public void reply(ArticleCommentRB articleCommentRB, CommentType commentType) {


        long articleFieldId = articleCommentRB.getArticleFieldId();
        if (!articleFieldRepository.existsByDeletedFalseAndAllowCommentTrueAndId(articleFieldId)) {
            throw new CodeException(CustomerErrorCode.ArticleCommentIsClose);
        }
        User user = new User();
        user.setId(userSupport.getCurrentUser().getId());

        ArticleField af = new ArticleField();
        af.setId(articleFieldId);

        if (articleCommentRB.getParentCommentId() == 0) {
            ArticleComment articleComment = ArticleComment.builder()
                    .parentCommentId(0)
                    .parentUserId(0)
                    .text(articleCommentRB.getText())
                    .commentType(commentType)
                    .user(user)
                    .articleField(af)
                    .ua(userSupport.getUserAgent())
                    .build();
            ArticleComment save = articleCommentRepository.save(articleComment);

            String content = articleCommentRB.getText().substring(0, Math.min(50, articleCommentRB.getText().length()));
            Optional<ArticleFieldInfo> t = articleFieldRepository.getTitle(articleFieldId);
            String title = "";
            if (t.isPresent()) {
                title = t.get().getTitle();
            }
            sendActionMqMessage(save.getId(), articleFieldId, commentType, false, content, title);

        } else {
            if (!articleCommentRepository.isFirstAnswer(articleCommentRB.getParentCommentId())) {
                throw new CodeException(CustomerErrorCode.ArticleCommentNotIsFirst);
            }
            if (articleCommentRepository.existsByDeletedFalseAndIdAndArticleFieldIdAndCommentType
                    (articleCommentRB.getParentCommentId(), articleFieldId, CommentType.comment)) {
                throw new CodeException(CustomerErrorCode.ArticleCommentNotFount);
            }
            ArticleComment articleComment = ArticleComment.builder()
                    .user(user)
                    .articleField(af)
                    .text(articleCommentRB.getText())
                    .parentCommentId(articleCommentRB.getParentCommentId())
                    .parentUserId(articleCommentRB.getParentUserId())
                    .commentType(commentType)
                    .ua(userSupport.getUserAgent())
                    .build();
            ArticleComment save = articleCommentRepository.save(articleComment);
            String content = articleCommentRB.getText().substring(0, Math.min(50, articleCommentRB.getText().length()));
            Optional<ArticleFieldInfo> t = articleFieldRepository.getTitle(articleFieldId);
            String title = "";
            if (t.isPresent()) {
                title = t.get().getTitle();
            }
            sendActionMqMessage(save.getId(),articleCommentRB.getParentUserId(), commentType, false, content, title);
        }
    }

    public boolean logicallyDelete(Long id) {
        Long uid = userSupport.getCurrentUser().getId();
        int i = articleCommentRepository.logicallyDelete(uid, id);
//        if (articleCommentRepository.isFirstAnswer(id)) {
//            sendActionMqMessage(articleCommentRepository.getArticleFieldById(id),id, CommentType.comment, true);
//        } else {
//            sendActionMqMessage(,id, CommentType.comment_comment, true);
//        }
        return i > 0;
    }

    public boolean logicallyRecovery(Long id) {
        Long uid = userSupport.getCurrentUser().getId();
        int i = articleCommentRepository.logicallyRecovery(uid, id);
//        if (articleCommentRepository.isFirstAnswer(id)) {
//            sendActionMqMessage(id, CommentType.comment, false);
//        } else {
//            sendActionMqMessage(id, CommentType.comment_comment, false);
//        }
        return i > 0;
    }


    public CommentType action(ArticleCommentActionRB commentActionRB) {
        Long uid = userSupport.getCurrentUser().getId();
        long fid = commentActionRB.getArticleFieldId();
        long pid = commentActionRB.getActionCommentId();
        if (pid < -1 || pid == 0) {
//            等于 -1  点赞 文章
            throw new CodeException(CustomerErrorCode.BodyError);
        }
        CommentType commentType = commentActionRB.getCommentType();
        if (commentType == CommentType.comment || commentType == CommentType.cancel) {
            throw new CodeException(CustomerErrorCode.ParamError);
        }
        if (articleCommentRepository.existsByDeletedFalseAndIdAndArticleFieldIdAndCommentType
                (commentActionRB.getActionCommentId(), commentActionRB.getArticleFieldId(), CommentType.comment)) {
            throw new CodeException(CustomerErrorCode.ArticleCommentNotFount);
        }


        if (articleCommentRepository
                .existsByDeletedFalseAndUser_IdAndArticleField_IdAndParentCommentIdAndCommentTypeNot(uid, fid, pid, CommentType.comment)) {
//            exists cancel
//            up -> cancel ->up state transfer base database ont is user action
//            one row is one action
            ArticleComment comment = articleCommentRepository
                    .findByDeletedFalseAndUser_IdAndArticleField_IdAndParentCommentIdAndCommentTypeNot(uid, fid, pid, CommentType.comment);
            if (comment.getCommentType() == commentType) {
                if (commentType == CommentType.up) {//相同2次操作取消
                    articleCommentRepository.upNumIncrement(fid, -1);
                } else {
                    articleCommentRepository.downNumIncrement(fid, -1);
                }

                comment.setCommentType(CommentType.cancel);
                sendActionMqMessage(pid, fid, commentType, true);

            } else {//点赞->点踩  先取消点赞 再点踩
                if (commentType == CommentType.up) {
                    articleCommentRepository.downNumIncrement(fid, -1);
                    articleCommentRepository.upNumIncrement(fid, 1);

                } else {
                    articleCommentRepository.downNumIncrement(fid, 1);
                    articleCommentRepository.upNumIncrement(fid, -1);
                }
                sendActionMqMessage(pid, fid, commentType, false);
                comment.setCommentType(commentType);
            }
            return comment.getCommentType();
        }

        User user = new User();
        user.setId(uid);
        ArticleField af = new ArticleField();
        af.setId(fid);

        long actionCommentId = commentActionRB.getActionCommentId();

        long ActionUserId;

        if (fid != -1) {
            ArticleComment actionComment = articleCommentRepository.findByDeletedFalseAndIdAndCommentType(actionCommentId, CommentType.comment);
            ActionUserId = actionComment.getUser().getId();
        } else {
            ActionUserId = 0;
            if (commentType == CommentType.up) {
                articleFieldRepository.upNumIncrement(fid, 1);
                sendActionMqMessage(pid, fid, commentType, false);

            } else {
                articleFieldRepository.downNumIncrement(fid, 1);
            }
        }


        ArticleComment articleComment = ArticleComment.builder()
                .parentCommentId(commentActionRB.getActionCommentId())
                .parentUserId(ActionUserId)
                .commentType(commentType)
                .user(user)
                .articleField(af)
                .ua(userSupport.getUserAgent())
                .build();
        articleCommentRepository.save(articleComment);

        return commentType;


    }

    private void sendActionMqMessage(long articleId, long commentId, CommentType commentType, boolean cancel, String formContent, String toContent) {
        UserCommentNotifyMessage activeMessage = UserCommentNotifyMessage.builder()
                .userActiveType(UserActiveType.get(commentType))

                .formUserId(userSupport.getCurrentUser().getId())

                .articleId(articleId)

                .commentId(commentId)

                .ua(userSupport.getUserAgent())


                .cancel(cancel)

                .formContent(formContent)

                .toContent(toContent)

                .build();
        rabbitTemplate.convertAndSend(UserActiveConstants.QUEUE_DDL_USER_ARTICLE_COMMENT_ACTIVE, activeMessage);
    }

    private void sendActionMqMessage(long articleId, long commentId, CommentType commentType, boolean cancel) {
        UserCommentNotifyMessage activeMessage = UserCommentNotifyMessage.builder()
                .userActiveType(UserActiveType.get(commentType))

                .formUserId(userSupport.getCurrentUser().getId())

                .articleId(articleId)

                .commentId(commentId)

                .ua(userSupport.getUserAgent())



                .cancel(cancel)

                .build();
        rabbitTemplate.convertAndSend(UserActiveConstants.QUEUE_DDL_USER_ARTICLE_COMMENT_ACTIVE, activeMessage);
    }


}


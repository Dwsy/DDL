package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.XO.Enum.Article.CommentType;
import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.XO.Message.UserCommentNotifyMessage;
import link.dwsy.ddl.XO.Projection.ArticleFieldInfo;
import link.dwsy.ddl.XO.RB.ArticleCommentActionRB;
import link.dwsy.ddl.XO.RB.ArticleCommentRB;
import link.dwsy.ddl.constants.mq.UserActiveMQConstants;
import link.dwsy.ddl.constants.task.RedisRecordHashKey;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.entity.Article.ArticleComment;
import link.dwsy.ddl.entity.Article.ArticleField;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.repository.Article.ArticleCommentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.service.ArticleCommentService;
import link.dwsy.ddl.service.Impl.ArticleRedisRecordService;
import link.dwsy.ddl.service.Impl.UserStateService;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.HtmlHelper;
import link.dwsy.ddl.util.PageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author Dwsy
 * @Date 2022/8/26
 */

@Service
@Slf4j
public class ArticleCommentServiceImpl implements ArticleCommentService {
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

    @Resource
    private UserStateService userStateService;

    @Resource
    private ArticleRedisRecordService articleRedisRecordService;

    @Override
    public PageData<ArticleComment> getByArticleId(long aid, PageRequest pageRequest) {
        Page<ArticleComment> parentComment = articleCommentRepository.
                findAllByDeletedIsFalseAndArticleFieldIdAndParentCommentId(aid, 0L, pageRequest);
        PageRequest pr = PageRequest.of(0, 8, Sort.by(Sort.Direction.ASC, "createTime"));
        for (ArticleComment articleComment : parentComment) {
            userStateService.cancellationUserHandel(articleComment.getUser());
            long pid = articleComment.getId();
            Page<ArticleComment> childComments = articleCommentRepository.
                    findByArticleField_IdAndParentCommentIdAndCommentTypeAndDeletedFalse
                            (aid, pid, CommentType.comment, pr);
            articleComment.setChildComments(childComments.getContent());
            articleComment.setChildCommentNum(childComments.getTotalElements());
            articleComment.setChildCommentTotalPages(childComments.getTotalPages());
            //添加点赞状态
            LoginUserInfo user = userSupport.getCurrentUser();
            if (user != null) {
                articleCommentRepository
                        .findByUserIdAndParentCommentIdAndCommentTypeIn(user.getId(), pid, Set.of(CommentType.up, CommentType.down))
                        .ifPresent(c -> articleComment.setUserAction(c.getCommentType()));
            }
            //添加子评论点赞状态 设置注销用户
            for (ArticleComment childComment : childComments.getContent()) {
                userStateService.cancellationUserHandel(childComment.getUser());
                if (user != null) {
                    articleCommentRepository.
                            findByUserIdAndParentCommentIdAndCommentTypeIn
                                    (user.getId(), childComment.getId(), Set.of(CommentType.up, CommentType.down))
                            .ifPresent(c -> childComment.setUserAction(c.getCommentType()));
                }
            }

        }

        return new PageData<>(parentComment);
    }

    @Override
    public PageData<ArticleComment> getChildCommentsByParentId(Long aid, Long pid, PageRequest pageRequest) {
        Page<ArticleComment> childComments = articleCommentRepository.
                findByArticleField_IdAndParentCommentIdAndCommentTypeAndDeletedFalse
                        (aid, pid, CommentType.comment, pageRequest);
        for (ArticleComment childComment : childComments) {
            userStateService.cancellationUserHandel(childComment.getUser());
        }
        return new PageData<>(childComments);
    }

    @Override
    public ArticleComment reply(ArticleCommentRB articleCommentRB, CommentType commentType) {

        long articleFieldId = articleCommentRB.getArticleFieldId();
        if (articleFieldRepository.userIsCancellation(articleFieldId) > 0) {
            throw new CodeException(CustomerErrorCode.ArticleCommentIsClose);
        }
        if (!articleFieldRepository.existsByDeletedFalseAndAllowCommentTrueAndId(articleFieldId)) {
            throw new CodeException(CustomerErrorCode.ArticleCommentIsClose);
        }
        User user = new User();
        user.setId(userSupport.getCurrentUser().getId());
        articleCommentRB.setText(HtmlHelper.filter(articleCommentRB.getText().trim()));
        ArticleField af = new ArticleField();
        af.setId(articleFieldId);
        int commentSerialNumber = 1;
        //评论文章
        if (articleCommentRB.getParentCommentId() == 0) {

            return replyArticle(articleCommentRB, commentType, articleFieldId, user, af, commentSerialNumber);
        } else {
            long parentCommentId = articleCommentRB.getParentCommentId();
            if (!articleCommentRepository.isFirstComment(parentCommentId)) {
                throw new CodeException(CustomerErrorCode.ArticleCommentNotIsFirst);
            }
            if (!articleCommentRepository.existsByDeletedFalseAndIdAndArticleFieldIdAndCommentType
                    (parentCommentId, articleFieldId, CommentType.comment)) {
                throw new CodeException(CustomerErrorCode.ArticleCommentNotFount);
            }
            ArticleComment lastComment = articleCommentRepository
                    .findFirstByDeletedFalseAndArticleField_IdAndParentCommentIdAndCommentTypeOrderByCommentSerialNumberDesc
                            (articleFieldId, parentCommentId, commentType);
            if (lastComment != null) {
                commentSerialNumber = lastComment.getCommentSerialNumber() + 1;
            }
            //回复评论

            if (articleCommentRB.getReplyUserCommentId() == 0) {
                return replyArticleComment(articleCommentRB, commentType, articleFieldId, user, af, commentSerialNumber, parentCommentId);
            } else {
                //回复二级评论
                return replyArticleSecondComment(articleCommentRB, commentType, articleFieldId, user, af, commentSerialNumber, parentCommentId);
            }
        }
    }


    public ArticleComment replyArticleSecondComment(ArticleCommentRB articleCommentRB, CommentType commentType, long articleFieldId, User user, ArticleField af, int commentSerialNumber, long parentCommentId) {
        String replyText;
//                if(articleCommentRepository.notIsSecondaryComment(articleCommentRB.getReplyUserCommentId())){
        replyText = "回复@" + userRepository.getUserNicknameById
                (articleCommentRB.getReplyUserId()) + "：" + articleCommentRB.getText();
//                }else {
//                    String str = articleCommentRB.getText().split("：")[1];
//                    replyText = "回复@" + userRepository.findUserNicknameById
//                            (articleCommentRB.getReplyUserId()) + "：" + str;
//                }
        ArticleComment articleComment = ArticleComment.builder()
                .user(user)
                .articleField(af)
                .text(replyText)
                .parentCommentId(parentCommentId)
                .parentUserId(articleCommentRB.getReplyUserId())
                .commentType(commentType)
                .replyUserCommentId(articleCommentRB.getReplyUserCommentId())
                .ua(userSupport.getUserAgent())
                .commentSerialNumber(commentSerialNumber)
                .build();
        String content = articleCommentRB.getText().substring(0, Math.min(100, articleCommentRB.getText().length()));
        String parentText = articleCommentRepository.getText(articleCommentRB.getReplyUserCommentId());
        ArticleComment save = articleCommentRepository.save(articleComment);
        sendActionMqMessage(user.getId(), articleFieldId, articleCommentRB.getReplyUserCommentId(),
                commentType, false, content, parentText, save.getId());
        articleFieldRepository.commentNumIncrement(articleFieldId, 1);
        articleRedisRecordService.record(articleFieldId, RedisRecordHashKey.comment, 1);
        return save;
    }

    public ArticleComment replyArticleComment(ArticleCommentRB articleCommentRB, CommentType commentType, long articleFieldId, User user, ArticleField af, int commentSerialNumber, long parentCommentId) {
        long replyUserId = articleCommentRB.getReplyUserId();
        if (userRepository.findById(replyUserId).isEmpty()) {
            throw new CodeException(CustomerErrorCode.UserNotExist);
        }

        ArticleComment articleComment = ArticleComment.builder()
                .user(user)
                .articleField(af)
                .text(articleCommentRB.getText())
                .parentCommentId(parentCommentId)
                .parentUserId(articleCommentRB.getReplyUserId())
                .commentType(commentType)
                .ua(userSupport.getUserAgent())
                .commentSerialNumber(commentSerialNumber)
                .build();
        ArticleComment save = articleCommentRepository.save(articleComment);
        String content = articleCommentRB.getText().substring(0, Math.min(100, articleCommentRB.getText().length()));
        String parentText = articleCommentRepository.getText(parentCommentId);
        sendActionMqMessage(user.getId(), articleFieldId, parentCommentId,
                commentType, false, content, parentText, save.getId());
        articleFieldRepository.commentNumIncrement(articleFieldId, 1);
        articleRedisRecordService.record(articleFieldId, RedisRecordHashKey.comment, 1);
        return save;
    }

    public ArticleComment replyArticle(ArticleCommentRB articleCommentRB, CommentType commentType, long articleFieldId, User user, ArticleField af, int commentSerialNumber) {
        ArticleComment lastComment = articleCommentRepository
                .findFirstByDeletedFalseAndArticleField_IdAndParentCommentIdAndCommentTypeOrderByCommentSerialNumberDesc
                        (articleFieldId, 0L, CommentType.comment);
        if (lastComment != null) {
            commentSerialNumber = lastComment.getCommentSerialNumber() + 1;
        }
        ArticleComment articleComment = ArticleComment.builder()
                .parentCommentId(0)
                .parentUserId(0)
                .text(articleCommentRB.getText())
                .commentType(commentType)
                .user(user)
                .articleField(af)
                .ua(userSupport.getUserAgent())
                .commentSerialNumber(commentSerialNumber)
                .build();
        ArticleComment save = articleCommentRepository.save(articleComment);

        String content = articleCommentRB.getText().substring(0, Math.min(100, articleCommentRB.getText().length()));
        Optional<ArticleFieldInfo> t = articleFieldRepository.getTitle(articleFieldId);
        String title = "";
        if (t.isPresent()) {
            title = t.get().getTitle();
        }

        sendActionMqMessage(user.getId(), articleFieldId, articleCommentRB.getParentCommentId(),
                commentType, false, content, title, save.getId());
        articleFieldRepository.commentNumIncrement(articleFieldId, 1);
        articleRedisRecordService.record(articleFieldId, RedisRecordHashKey.comment, 1);
        return save;
    }

    @Override
    public boolean logicallyDelete(long articleId, long commentId) {
        Long uid = userSupport.getCurrentUser().getId();

        Long articleUserId = articleFieldRepository.findUserIdById(articleId);

        Long commentUserId = articleCommentRepository.getUserIdByCommentId(commentId);

        AtomicInteger delCommentNum = new AtomicInteger();
        if (uid.equals(articleUserId) || uid.equals(commentUserId)) {
            Optional<ArticleComment> commentOptional = articleCommentRepository.findByDeletedFalseAndId(commentId);
            if (commentOptional.isPresent()) {
                ArticleComment comment = commentOptional.get();
                if (comment.getParentCommentId() == 0) {//一级评论
                    log.info("delete comment:{}", commentId);
                    articleCommentRepository
                            .findByParentCommentIdAndDeletedFalseAndCommentTypeIn
                                    (commentId, Arrays.asList
                                            (CommentType.comment, CommentType.up, CommentType.down, CommentType.cancel))
                            .forEach(c -> {
                                if (c.getCommentType() == CommentType.comment) {
                                    articleCommentRepository
                                            .findByParentCommentIdAndDeletedFalseAndCommentTypeIn
                                                    (c.getId(), Arrays.asList
                                                            (CommentType.up, CommentType.down, CommentType.cancel))
                                            .forEach(cc -> {
                                                cc.setDeleted(true);
                                                articleCommentRepository.save(cc);
                                            });
                                }
                                c.setDeleted(true);
                                delCommentNum.getAndIncrement();
                                articleCommentRepository.save(c);
                            });
                    comment.setDeleted(true);
                    delCommentNum.getAndIncrement();
                    articleFieldRepository.commentNumIncrement(articleId, -delCommentNum.get());
                    articleCommentRepository.save(comment);
                    return true;
                }
                if (comment.getParentCommentId() > 0) {//二级评论
                    articleCommentRepository
                            .findByParentCommentIdAndDeletedFalseAndCommentTypeIn
                                    (commentId, Arrays.asList
                                            (CommentType.up, CommentType.down, CommentType.cancel))
                            .forEach(c -> {
                                c.setDeleted(true);
                                articleCommentRepository.save(c);
                            });
                    articleFieldRepository.commentNumIncrement(articleId, -1);
                    articleCommentRepository.save(comment);
                    return true;
                }
            }
        } else {
            throw new CodeException(CustomerErrorCode.ArticleCommentNotFount);
        }
        return false;
    }

    @Override
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


    @Override
    public CommentType action(ArticleCommentActionRB commentActionRB) {
        long fid = commentActionRB.getArticleFieldId();
        if (articleFieldRepository.userIsCancellation(fid) > 0) {
            throw new CodeException(CustomerErrorCode.ArticleCommentIsClose);
        }
        Long uid = userSupport.getCurrentUser().getId();
        long pid = commentActionRB.getActionCommentId();
        if (pid < -1 || pid == 0) {
//            等于 -1  点赞 文章
            throw new CodeException(CustomerErrorCode.BodyError);
        }
        boolean actionArticle = pid == -1;
        CommentType commentType = commentActionRB.getCommentType();
        if (commentType == CommentType.comment || commentType == CommentType.cancel) {
            throw new CodeException(CustomerErrorCode.BodyError);
        }
        if (!actionArticle) {
            if (!articleCommentRepository.existsByDeletedFalseAndIdAndArticleFieldIdAndCommentType
                    (commentActionRB.getActionCommentId(), commentActionRB.getArticleFieldId(), CommentType.comment)) {
                throw new CodeException(CustomerErrorCode.ArticleCommentNotFount);
            }
        } else {
            if (!articleFieldRepository.existsById(fid)) {
                throw new CodeException(CustomerErrorCode.ArticleCommentNotFount);
            }
        }


        if (articleCommentRepository
                .existsByDeletedFalseAndUser_IdAndArticleField_IdAndParentCommentIdAndCommentTypeNot
                        (uid, fid, pid, CommentType.comment)) {

//            exists cancel
//            up -> cancel ->up state transfer base database ont is user action
//            one row is one action

            return ifExistAction(fid, uid, pid, actionArticle, commentType);
        }

        User user = new User();
        user.setId(uid);
        ArticleField af = new ArticleField();
        af.setId(fid);

        long actionCommentId = commentActionRB.getActionCommentId();

        long ActionUserId;

        if (!actionArticle) {// -1 是啥玩意  想起来料 -1是点赞or点踩文章 0为评论文章这样查询可以少个类型判断
            ArticleComment actionComment = articleCommentRepository.
                    findByDeletedFalseAndIdAndCommentType(actionCommentId, CommentType.comment);
            ActionUserId = actionComment.getUser().getId();
            articleCommentRepository.upNumIncrement(actionCommentId, 1);
            sendActionMqMessage(uid, fid, pid, commentType);
        } else {
            ActionUserId = 0;//文章 避免前端参数错误 后端直接不管了 要用到时候从文章id查询用户id
            if (commentType == CommentType.up) {
                articleFieldRepository.upNumIncrement(fid, 1);
                articleRedisRecordService.record(fid, RedisRecordHashKey.up, 1);
                sendActionMqMessage(uid, fid, pid, commentType);

            } else {
                articleFieldRepository.downNumIncrement(fid, 1);
                articleRedisRecordService.record(fid, RedisRecordHashKey.down, 1);
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

    private CommentType ifExistAction(long fid, Long uid, long pid, boolean actionArticle, CommentType commentType) {
        ArticleComment comment = articleCommentRepository
                .findByDeletedFalseAndUser_IdAndArticleField_IdAndParentCommentIdAndCommentTypeNot
                        (uid, fid, pid, CommentType.comment);
        if (comment.getCommentType() == CommentType.cancel) {
            if (commentType == CommentType.up) {
                if (actionArticle) {
                    articleFieldRepository.upNumIncrement(fid, 1);
                    articleRedisRecordService.record(fid, RedisRecordHashKey.up, 1);
                } else {
                    articleCommentRepository.upNumIncrement(pid, 1);
                    articleCommentRepository.
                            updateCommentTypeByIdAndDeletedFalse(commentType, comment.getId());
                }
                comment.setCommentType(commentType);
                articleCommentRepository.save(comment);
                sendActionMqMessage(uid, fid, pid, commentType);
            } else if (commentType == CommentType.down) {
                if (actionArticle) {
                    articleFieldRepository.downNumIncrement(fid, 1);
                    articleRedisRecordService.record(fid, RedisRecordHashKey.down, 1);
                } else {
                    articleCommentRepository.downNumIncrement(pid, 1);
                    articleCommentRepository.
                            updateCommentTypeByIdAndDeletedFalse(commentType, comment.getId());
                }
                articleRedisRecordService.record(fid, RedisRecordHashKey.up, -1);
                comment.setCommentType(commentType);
                articleCommentRepository.save(comment);
            }
            return comment.getCommentType();
        }

        if (comment.getCommentType() == commentType) {
            if (commentType == CommentType.up) {//相同2次操作取消
                if (actionArticle) {
                    articleFieldRepository.upNumIncrement(fid, -1);
                    articleRedisRecordService.record(fid, RedisRecordHashKey.up, -1);
                } else {
                    articleCommentRepository.upNumIncrement(pid, -1);//取消点赞-1
                }
                sendActionMqMessage(uid, fid, pid, CommentType.cancel);
//                    articleCommentRepository.updateCommentTypeByIdAndDeletedFalse(CommentType.cancel, comment.getId());
            } else {
                if (actionArticle) {
                    articleFieldRepository.downNumIncrement(fid, -1);
                    articleRedisRecordService.record(fid, RedisRecordHashKey.down, 1);
                } else {
                    articleCommentRepository.downNumIncrement(pid, -1); //取消踩  +1
                }

//                    articleCommentRepository.updateCommentTypeByIdAndDeletedFalse(CommentType.cancel, comment.getId());
            }

            comment.setCommentType(CommentType.cancel);


        } else {//点踩->点赞 / 点赞->点踩  先取消点赞 再点踩 返回叠加状态 to
            if (commentType == CommentType.up) {
                if (actionArticle) {
                    articleFieldRepository.downNumIncrement(fid, -1);
                    articleFieldRepository.upNumIncrement(fid, 1);
                    articleRedisRecordService.record(fid, RedisRecordHashKey.up, 1);
                    articleRedisRecordService.record(fid, RedisRecordHashKey.down, -1);
                } else {
                    articleCommentRepository.downNumIncrement(pid, -1);
                    articleCommentRepository.upNumIncrement(pid, 1);
                }
                sendActionMqMessage(uid, fid, pid, commentType);
                comment.setCommentType(CommentType.up);
                articleCommentRepository.save(comment);
                return CommentType.downToUp;
            } else {
                if (actionArticle) {
                    articleFieldRepository.upNumIncrement(fid, -1);
                    articleFieldRepository.downNumIncrement(fid, 1);
                    articleRedisRecordService.record(fid, RedisRecordHashKey.up, -1);
                    articleRedisRecordService.record(fid, RedisRecordHashKey.down, 1);
                } else {
                    articleCommentRepository.upNumIncrement(pid, -1);
                    articleCommentRepository.downNumIncrement(pid, 1);
                }
                sendActionMqMessage(uid, fid, pid, commentType);
                comment.setCommentType(CommentType.down);
                articleCommentRepository.save(comment);
                return CommentType.upToDown;
            }


        }
        articleCommentRepository.save(comment);
        return comment.getCommentType();
    }

    private void sendActionMqMessage(long userId, long articleFieldId, long parentCommentId,
                                     CommentType commentType, boolean cancel, String formContent, String toContent, long replyCommentId) {
//        评论
        UserCommentNotifyMessage activeMessage = UserCommentNotifyMessage.builder()
                .userActiveType(UserActiveType.Converter(commentType, parentCommentId))

                .formUserId(userId)

                .articleId(articleFieldId)

                .commentId(parentCommentId)

                .ua(userSupport.getUserAgent())


                .cancel(cancel)

                .formContent(formContent)

                .toContent(toContent)

                .replyCommentId(replyCommentId)

                .build();
        rabbitTemplate.convertAndSend(UserActiveMQConstants.QUEUE_DDL_USER_ARTICLE_COMMENT_ACTIVE, activeMessage);
    }

    private void sendActionMqMessage(long userId, long articleFieldId, long parentCommentId, CommentType commentType) {
        UserCommentNotifyMessage activeMessage = UserCommentNotifyMessage.builder()
                .userActiveType(UserActiveType.Converter(commentType, parentCommentId))

                .formUserId(userId)

                .articleId(articleFieldId)

                .commentId(parentCommentId)

                .ua(userSupport.getUserAgent())

                .cancel(commentType == CommentType.cancel)

                .build();
        rabbitTemplate.convertAndSend(UserActiveMQConstants.QUEUE_DDL_USER_ARTICLE_COMMENT_ACTIVE, activeMessage);
    }


}


package link.dwsy.ddl.service.impl;

import cn.hutool.crypto.SecureUtil;
import link.dwsy.ddl.XO.Enum.Message.NotifyType;
import link.dwsy.ddl.XO.Enum.QA.AnswerType;
import link.dwsy.ddl.XO.Enum.QA.QuestionState;
import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.XO.Message.Question.InvitationUserAnswerQuestionMsg;
import link.dwsy.ddl.XO.Message.UserQuestionAnswerNotifyMessage;
import link.dwsy.ddl.XO.RB.InvitationUserRB;
import link.dwsy.ddl.XO.RB.QaAnswerRB;
import link.dwsy.ddl.XO.RB.TagIdsRB;
import link.dwsy.ddl.XO.VO.InvitationUserVO;
import link.dwsy.ddl.XO.VO.UserAnswerVO;
import link.dwsy.ddl.constants.mq.UserActiveMQConstants;
import link.dwsy.ddl.controller.QuestionAnswerOrCommentActionRB;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.core.domain.R;
import link.dwsy.ddl.entity.QA.QaAnswer;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.entity.User.UserNotify;
import link.dwsy.ddl.repository.QA.QaAnswerRepository;
import link.dwsy.ddl.repository.QA.QaQuestionFieldRepository;
import link.dwsy.ddl.repository.User.UserNotifyRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.service.Impl.UserStateService;
import link.dwsy.ddl.service.QaAnswerService;
import link.dwsy.ddl.service.RPC.UserService;
import link.dwsy.ddl.service.RPC.UserTagService;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.HtmlHelper;
import link.dwsy.ddl.util.PageData;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author Dwsy
 * @Date 2022/8/26
 */

@Service
public class QaAnswerServiceServiceImpl implements QaAnswerService {
    @Resource
    private QaQuestionFieldRepository qaQuestionFieldRepository;

    @Resource
    private QaAnswerRepository qaAnswerRepository;

    @Resource
    private UserSupport userSupport;

    @Resource
    private UserRepository userRepository;


    @Resource
    private UserNotifyRepository userNotifyRepository;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private UserStateService userStateService;
    @Resource
    private UserTagService userTagService;
    @Resource
    private UserService userService;

    public PageData<QaAnswer> getByQuestionId(long qid, PageRequest pageRequest) {
        Page<QaAnswer> QaAnswerData = qaAnswerRepository
                .findByDeletedFalseAndQuestionField_IdAndAnswerTypeAndParentAnswerId(qid, AnswerType.answer, 0L, pageRequest);
        PageRequest pr = PageRequest.of(0, 8, Sort.by(Sort.Direction.ASC, "createTime"));
        for (QaAnswer qaAnswer : QaAnswerData) {
            userStateService.cancellationUserHandel(qaAnswer.getUser());
            long pid = qaAnswer.getId();
            Page<QaAnswer> childQaAnswersPage = qaAnswerRepository
                    .findByDeletedFalseAndQuestionField_IdAndAnswerTypeAndParentAnswerId(qid, AnswerType.answer_comment, pid, pr);
            qaAnswer.setChildQaAnswers(childQaAnswersPage.getContent());
            qaAnswer.setChildQaAnswerTotalPages(childQaAnswersPage.getTotalPages());
            qaAnswer.setChildQaAnswerNum(childQaAnswersPage.getTotalElements());
            //添加点赞状态
            LoginUserInfo user = userSupport.getCurrentUser();
            if (user != null) {
                qaAnswerRepository.
                        findByDeletedFalseAndUser_IdAndParentAnswerIdAndAnswerTypeIn
                                (user.getId(), pid,
                                        Set.of(AnswerType.up, AnswerType.down))
                        .ifPresent(c -> qaAnswer.setUserAction(c.getAnswerType()));
            }
            //添加子评论点赞状态
            for (QaAnswer childQaAnswer : childQaAnswersPage.getContent()) {
                userStateService.cancellationUserHandel(childQaAnswer.getUser());
                if (user != null) {
                    qaAnswerRepository.findByDeletedFalseAndUser_IdAndParentAnswerIdAndAnswerTypeIn
                                    (user.getId(), childQaAnswer.getId(), Set.of(AnswerType.up, AnswerType.down))
                            .ifPresent(c -> childQaAnswer.setUserAction(c.getAnswerType()));
                }
            }

        }
        return new PageData<>(QaAnswerData);
    }

    public PageData<QaAnswer> getChildAnswerPageByParentId(Long qid, Long pid, PageRequest pageRequest) {
        Page<QaAnswer> childQaAnswersPage = qaAnswerRepository
                .findAllByDeletedIsFalseAndQuestionFieldIdAndParentAnswerId(qid, pid, pageRequest);
        for (QaAnswer qaAnswer : childQaAnswersPage) {
            userStateService.cancellationUserHandel(qaAnswer.getUser());
        }

        return new PageData<>(childQaAnswersPage);
    }

    public QaAnswer answer(QaAnswerRB qaAnswerRB, AnswerType answerType) {

        //todo answer
        long questionFieldId = qaAnswerRB.getQuestionId();
        if (qaQuestionFieldRepository.userIsCancellation(questionFieldId) > 0) {
            throw new CodeException(CustomerErrorCode.QuestionNotFound);
        }
        if (!qaQuestionFieldRepository.existsByDeletedFalseAndAllowAnswerTrueAndId(questionFieldId)) {
            throw new CodeException(CustomerErrorCode.QuestionNotFoundOrClose);
        }
        User user = new User();
        user.setId(userSupport.getCurrentUser().getId());
        QaQuestionField qaQuestionField = new QaQuestionField();
        qaQuestionField.setId(questionFieldId);
        int answerSerialNumber = 1;

        long parentAnswerId = qaAnswerRB.getParentAnswerId();
        if (parentAnswerId == 0) {//问题
            if (answerType == AnswerType.answer) {//回答
                return answerQuestion(qaAnswerRB, answerType, questionFieldId, user, qaQuestionField, answerSerialNumber);
            } else if (answerType == AnswerType.comment) {//回复询问细节
                return addComment(qaAnswerRB, answerType, questionFieldId, user, qaQuestionField, answerSerialNumber);
            }
        } else {
            long replyUserId = qaAnswerRB.getReplyUserId();
            if (!qaAnswerRepository.isFirstAnswer(parentAnswerId)) {
                throw new CodeException(CustomerErrorCode.ArticleCommentNotIsFirst);//todo
            }

            if (!qaAnswerRepository
                    .existsByDeletedFalseAndIdAndQuestionField_IdAndAnswerType(parentAnswerId, questionFieldId, AnswerType.answer)) {
                throw new CodeException(CustomerErrorCode.ArticleCommentNotFount);
            }

            QaAnswer lastAnswer = qaAnswerRepository
                    .findFirstByDeletedFalseAndQuestionField_IdAndParentAnswerIdAndAnswerTypeOrderByAnswerSerialNumberDesc
                            (questionFieldId, parentAnswerId, AnswerType.answer_comment);
            if (lastAnswer != null) {
                answerSerialNumber = lastAnswer.getAnswerSerialNumber() + 1;
            }
            if (answerType == AnswerType.answer_comment) {

                if (qaAnswerRB.getReplyUserAnswerId() == 0) {//回复询问细节
                    return replyComment(qaAnswerRB, answerType, questionFieldId, user, qaQuestionField, answerSerialNumber, parentAnswerId, replyUserId);
                } else {
                    //回复二级评论
                    return replySecondComment(qaAnswerRB, answerType, questionFieldId, user, qaQuestionField, answerSerialNumber, replyUserId);
                }
            }

        }
        throw new CodeException(CustomerErrorCode.BodyError);
    }

    private QaAnswer replySecondComment(QaAnswerRB qaAnswerRB, AnswerType answerType, long questionFieldId, User user, QaQuestionField qaQuestionField, int answerSerialNumber, long replyUserId) {
        String replyText;

        replyText = "回复@" + userRepository.getUserNicknameById
                (qaAnswerRB.getReplyUserId()) + "：" + qaAnswerRB.getMdText();
        QaAnswer qaAnswer = QaAnswer.builder()
                .user(user)
                .questionField(qaQuestionField)
                .textHtml(replyText)
                .textMd(null)
                .parentAnswerId(qaAnswerRB.getParentAnswerId())
                .parentUserId(replyUserId)
                .answerType(answerType)
                .replyUserAnswerId(qaAnswerRB.getParentAnswerId())
                .ua(userSupport.getUserAgent())
                .answerSerialNumber(answerSerialNumber)
                .build();

        QaAnswer save = qaAnswerRepository.save(qaAnswer);
        String textHtml = qaAnswer.getTextHtml();
        String content = textHtml.substring(0, Math.min(200, textHtml.length()));
        String parentText = qaAnswerRepository.getHtmlText(qaAnswerRB.getParentAnswerId());

        sendActionMqMessage(user.getId(), questionFieldId, qaAnswerRB.getReplyUserAnswerId(),
                answerType, false, content, parentText, save.getId());
        return save;
    }

    private QaAnswer replyComment
            (QaAnswerRB qaAnswerRB, AnswerType answerType, long questionFieldId, User user, QaQuestionField qaQuestionField, int answerSerialNumber, long parentAnswerId, long replyUserId) {
        //todo
        if (replyUserId == 0) {
            replyUserId = qaAnswerRepository.getUserIdByAnswerId(parentAnswerId);
        }

        if (userRepository.findById(replyUserId).isEmpty()) {
            throw new CodeException(CustomerErrorCode.UserNotExist);
        }
        String mdText = qaAnswerRB.getMdText();
        QaAnswer qaAnswer = QaAnswer.builder()
                .user(user)
                .questionField(qaQuestionField)
                .textHtml(mdText)
                .textMd(null)
                .parentAnswerId(qaAnswerRB.getParentAnswerId())
                .parentUserId(replyUserId)
                .answerType(answerType)
                .ua(userSupport.getUserAgent())
                .answerSerialNumber(answerSerialNumber)
                .build();
        QaAnswer save = qaAnswerRepository.save(qaAnswer);

        String content = mdText.substring(0, Math.min(200, mdText.length()));
        String parentText = HtmlHelper.toPure(qaAnswerRepository.getHtmlText(qaAnswerRB.getParentAnswerId()));
        sendActionMqMessage(user.getId(), questionFieldId, parentAnswerId,
                answerType, false, content, parentText, save.getId());
        return save;
    }

    private QaAnswer answerQuestion(QaAnswerRB qaAnswerRB, AnswerType answerType, long questionFieldId, User user, QaQuestionField qaQuestionField, int answerSerialNumber) {
        QaAnswer lastAnswer = qaAnswerRepository
                .findFirstByDeletedFalseAndQuestionField_IdAndParentAnswerIdAndAnswerTypeOrderByAnswerSerialNumberDesc
                        (questionFieldId, 0L, AnswerType.answer);
        if (lastAnswer != null) {
            answerSerialNumber = lastAnswer.getAnswerSerialNumber() + 1;
        }
        String toHTML;
        toHTML = HtmlHelper.toHTML(qaAnswerRB.getMdText());
        QaAnswer qaAnswer = QaAnswer.builder()
                .parentAnswerId(0)
                .parentUserId(0)
                .textHtml(toHTML)
                .textMd(qaAnswerRB.getMdText())
                .answerType(answerType)
                .user(user)
                .questionField(qaQuestionField)
                .ua(userSupport.getUserAgent())
                .answerSerialNumber(answerSerialNumber)
                .build();
        QaAnswer save = qaAnswerRepository.save(qaAnswer);
        long answerId = save.getId();
        String substring = SecureUtil.md5(String.valueOf(answerId)).substring(10, 14);
        HtmlHelper.LinkRefAttributeProvider.answerId.set(substring);
//        qaAnswerRepository.seAnswerHtml(answerId, toHTML);
        //todo mq notice
        String toPure = HtmlHelper.toPure(toHTML);
        String content = toPure.substring(0, Math.min(200, toPure.length()));

        String title = qaQuestionFieldRepository.getTitle(questionFieldId);
//            if (articleFieldRepository.findUserIdById(articleFieldId)!=user.getId()){
        sendActionMqMessage(user.getId(), questionFieldId, qaAnswerRB.getParentAnswerId(),
                answerType, false, content, title, answerId);
//            }
        qaQuestionFieldRepository.answerNumIncrement(questionFieldId, 1);
        qaQuestionFieldRepository.setQuestionStateIfNowStateIs(questionFieldId, QuestionState.HAVE_ANSWER.ordinal(), QuestionState.ASK.ordinal());
        return save;
    }

    private QaAnswer addComment(QaAnswerRB qaAnswerRB, AnswerType answerType, long questionFieldId, User user, QaQuestionField qaQuestionField, int answerSerialNumber) {
        if (qaAnswerRB.getReplyUserAnswerId() == 0) {
            QaAnswer lastAnswer = qaAnswerRepository
                    .findFirstByDeletedFalseAndQuestionField_IdAndParentAnswerIdAndAnswerTypeOrderByAnswerSerialNumberDesc
                            (questionFieldId, 0L, AnswerType.comment);
            if (lastAnswer != null) {
                answerSerialNumber = lastAnswer.getAnswerSerialNumber() + 1;
            }

            QaAnswer qaAnswer = QaAnswer.builder()
                    .parentAnswerId(0)
                    .parentUserId(0)
                    .textHtml(qaAnswerRB.getMdText())//回复为纯文本
                    .textMd(null)
                    .answerType(answerType)
                    .user(user)
                    .questionField(qaQuestionField)
                    .ua(userSupport.getUserAgent())
                    .answerSerialNumber(answerSerialNumber)
                    .build();

            QaAnswer save = qaAnswerRepository.save(qaAnswer);

            //todo mq notice

            String textHtml = qaAnswer.getTextHtml();
            String content = textHtml.substring(0, Math.min(200, textHtml.length()));
            String title = qaQuestionFieldRepository.getTitle(questionFieldId);
//            if (articleFieldRepository.findUserIdById(articleFieldId)!=user.getId()){
            sendActionMqMessage(user.getId(), questionFieldId, qaAnswerRB.getParentAnswerId(),
                    answerType, false, content, title, save.getId());
//            }
//                qaQuestionFieldRepository.answerNumIncrement(questionFieldId, 1);
            return save;
        } else {
            return replyQuestionSecondComment(qaAnswerRB, answerType, questionFieldId, user, qaQuestionField, answerSerialNumber);
        }

    }

    private QaAnswer replyQuestionSecondComment(QaAnswerRB qaAnswerRB, AnswerType answerType, long questionFieldId, User user, QaQuestionField qaQuestionField, int answerSerialNumber) {
        String replyText;

        replyText = "回复@" + userRepository.getUserNicknameById
                (qaAnswerRB.getReplyUserId()) + "：" + qaAnswerRB.getMdText();
        QaAnswer lastAnswer = qaAnswerRepository
                .findFirstByDeletedFalseAndQuestionField_IdAndParentAnswerIdAndAnswerTypeOrderByAnswerSerialNumberDesc
                        (questionFieldId, 0L, AnswerType.comment);
        if (lastAnswer != null) {
            answerSerialNumber = lastAnswer.getAnswerSerialNumber() + 1;
        }

        QaAnswer qaAnswer = QaAnswer.builder()
                .parentAnswerId(0)
                .parentUserId(0)
                .textHtml(replyText)//回复为纯文本
                .textMd(null)
                .answerType(answerType)
                .user(user)
                .questionField(qaQuestionField)
                .ua(userSupport.getUserAgent())
                .answerSerialNumber(answerSerialNumber)
                .parentUserId(qaAnswerRB.getReplyUserId())
                .replyUserAnswerId(qaAnswerRB.getReplyUserAnswerId())
                .build();

        QaAnswer save = qaAnswerRepository.save(qaAnswer);

        //todo mq notice

        String textHtml = qaAnswer.getTextHtml();
        String content = textHtml.substring(0, Math.min(200, textHtml.length()));
        String title = qaQuestionFieldRepository.getTitle(questionFieldId);
//            if (articleFieldRepository.findUserIdById(articleFieldId)!=user.getId()){
        sendActionMqMessage(user.getId(), questionFieldId, qaAnswerRB.getParentAnswerId(),
                answerType, false, content, title, save.getId());
//            }
//                qaQuestionFieldRepository.answerNumIncrement(questionFieldId, 1);
        return save;
    }

    public boolean logicallyDelete(long questionId, long answerId) {
        return true;
    }

    private void sendActionMqMessage(long userId, long questionFieldId, long parentAnswerId,
                                     AnswerType answerType, boolean cancel, String formContent, String toContent, long replayAnswerId) {
//        评论
        UserQuestionAnswerNotifyMessage activeMessage = UserQuestionAnswerNotifyMessage.builder()
                .userActiveType(UserActiveType.Converter(answerType, parentAnswerId))

                .formUserId(userId)

                .questionId(questionFieldId)

                .answerId(parentAnswerId)

                .ua(userSupport.getUserAgent())

                .cancel(cancel)

                .formContent(formContent)

                .toContent(toContent)

                .replayAnswerId(replayAnswerId)

                .build();
        rabbitTemplate.convertAndSend
                (UserActiveMQConstants.QUEUE_DDL_USER_QUESTION_ANSWER_OR_COMMENT_ACTIVE, activeMessage);
    }

    private void sendThumbUpActionMqMessage(long userId, long questionFieldId, long parentAnswerId, AnswerType answerType) {
        UserQuestionAnswerNotifyMessage activeMessage = UserQuestionAnswerNotifyMessage.builder()
                .userActiveType(UserActiveType.Converter(answerType, parentAnswerId))

                .formUserId(userId)


                .questionId(questionFieldId)

                .answerId(parentAnswerId)

                .ua(userSupport.getUserAgent())

                .cancel(answerType == AnswerType.cancel)

                .build();

        rabbitTemplate.convertAndSend
                (UserActiveMQConstants.QUEUE_DDL_USER_QUESTION_ANSWER_OR_COMMENT_ACTIVE, activeMessage);
    }

    public AnswerType action(QuestionAnswerOrCommentActionRB actionRB) {
        long questionFieldId = actionRB.getQuestionFieldId();
        if (qaQuestionFieldRepository.userIsCancellation(questionFieldId) > 0) {
            throw new CodeException(CustomerErrorCode.QuestionNotFound);
        }
        Long uid = userSupport.getCurrentUser().getId();
        long actionAnswerOrCommentId = actionRB.getActionAnswerOrCommentId();
        if (actionAnswerOrCommentId < -1 || actionAnswerOrCommentId == 0) {
//            等于 -1  点赞 文章
            throw new CodeException(CustomerErrorCode.BodyError);
        }
        boolean actionQuestion = actionAnswerOrCommentId == -1;
//        CommentType commentType = commentActionRB.getCommentType();
        AnswerType answerType = actionRB.getAnswerType();
        List<AnswerType> thumbAnswerType = Arrays.asList(AnswerType.up, AnswerType.down);
        if (!thumbAnswerType.contains(answerType)) {
            throw new CodeException(CustomerErrorCode.BodyError);
        }
        if (!actionQuestion) {
            if (!qaAnswerRepository.existsByDeletedFalseAndIdAndQuestionField_IdAndAnswerTypeIn
                    (actionRB.getActionAnswerOrCommentId(), questionFieldId,
                            Arrays.asList(AnswerType.answer, AnswerType.comment))) {

                throw new CodeException(CustomerErrorCode.QuestionAnswerOrCommentNotFount);
            }
        } else {
            if (!qaQuestionFieldRepository.existsById(questionFieldId)) {
                throw new CodeException(CustomerErrorCode.QuestionAnswerOrCommentNotFount);
            }
        }

        if (qaAnswerRepository
                .existsByDeletedFalseAndUser_IdAndQuestionField_IdAndParentAnswerIdAndAnswerTypeIn
                        (uid, questionFieldId, actionAnswerOrCommentId,
                                Arrays.asList(AnswerType.up, AnswerType.down, AnswerType.cancel))) {
//            exists cancel
//            up -> cancel ->up state transfer base database ont is user action
//            one row is one action

            return existsAction(uid, questionFieldId, actionAnswerOrCommentId, actionQuestion, answerType);
        }
        User user = new User();
        user.setId(uid);
        QaQuestionField qf = new QaQuestionField();
        qf.setId(questionFieldId);

        long ActionUserId;
        if (!actionQuestion) {// -1是点赞or点踩文章 0为评论文章
            QaAnswer actionAnswer = qaAnswerRepository.findByDeletedFalseAndIdAndAnswerType
                    (actionAnswerOrCommentId, AnswerType.answer);

            ActionUserId = actionAnswer.getUser().getId();
            qaAnswerRepository.upNumIncrement(actionAnswerOrCommentId, 1);
            sendThumbUpActionMqMessage(uid, questionFieldId, actionAnswerOrCommentId, answerType);
        } else {
            ActionUserId = 0;//文章 避免前端参数错误 后端直接不管了 要用到时候从文章id查询用户id
            if (answerType == AnswerType.up) {
                qaQuestionFieldRepository.upNumIncrement(questionFieldId, 1);
                sendThumbUpActionMqMessage(uid, questionFieldId, actionAnswerOrCommentId, answerType);
            } else {
                qaQuestionFieldRepository.downNumIncrement(questionFieldId, 1);
            }
        }


        QaAnswer build = QaAnswer.builder()
                .parentAnswerId(actionAnswerOrCommentId)
                .parentUserId(ActionUserId)
                .answerType(answerType)
                .user(user)
                .questionField(qf)
                .ua(userSupport.getUserAgent())
                .build();
        qaAnswerRepository.save(build);
        return answerType;
    }

    private AnswerType existsAction(Long uid, long questionFieldId, long actionAnswerOrCommentId, boolean actionQuestion, AnswerType answerType) {
        QaAnswer action = qaAnswerRepository
                .findByDeletedFalseAndUser_IdAndQuestionField_IdAndParentAnswerIdAndAnswerTypeIn
                        (uid, questionFieldId, actionAnswerOrCommentId,
                                Arrays.asList(AnswerType.up, AnswerType.down, AnswerType.cancel));
        if (action.getAnswerType() == AnswerType.cancel) {
            if (answerType == AnswerType.up) {
                if (actionQuestion) {
                    qaQuestionFieldRepository.upNumIncrement(questionFieldId, 1);
                } else {
                    qaAnswerRepository.upNumIncrement(actionAnswerOrCommentId, 1);
                    qaAnswerRepository.updateCommentTypeByIdAndDeletedFalse(answerType, action.getId());
                }
                action.setAnswerType(answerType);
                qaAnswerRepository.save(action);
                sendThumbUpActionMqMessage(uid, questionFieldId, actionAnswerOrCommentId, answerType);
            } else if (answerType == AnswerType.down) {
                if (actionQuestion) {
                    qaQuestionFieldRepository.downNumIncrement(questionFieldId, 1);
                } else {
                    qaAnswerRepository.downNumIncrement(actionAnswerOrCommentId, 1);
                    qaAnswerRepository.updateCommentTypeByIdAndDeletedFalse(answerType, action.getId());
                }
                action.setAnswerType(answerType);
                qaAnswerRepository.save(action);
            }
            return action.getAnswerType();
        }

        if (action.getAnswerType() == answerType) {
            if (answerType == AnswerType.up) {//相同2次操作取消
                if (actionQuestion) {
                    qaQuestionFieldRepository.upNumIncrement(questionFieldId, -1);
                } else {
                    qaAnswerRepository.upNumIncrement(actionAnswerOrCommentId, -1);
                }
                sendThumbUpActionMqMessage(uid, questionFieldId, actionAnswerOrCommentId, AnswerType.cancel);
            } else {
                if (actionQuestion) {
                    qaQuestionFieldRepository.downNumIncrement(questionFieldId, -1);
                } else {
                    qaAnswerRepository.downNumIncrement(actionAnswerOrCommentId, -1); //取消踩  +1
                }
            }
            action.setAnswerType(AnswerType.cancel);
        } else {//点踩->点赞 / 点赞->点踩  先取消点赞 再点踩 返回叠加状态 to

            if (answerType == AnswerType.up) {
                if (actionQuestion) {
                    qaQuestionFieldRepository.downNumIncrement(questionFieldId, -1);
                    qaQuestionFieldRepository.upNumIncrement(questionFieldId, 1);
                } else {
                    qaAnswerRepository.downNumIncrement(actionAnswerOrCommentId, -1);
                    qaAnswerRepository.upNumIncrement(actionAnswerOrCommentId, 1);
                }
                sendThumbUpActionMqMessage(uid, questionFieldId, actionAnswerOrCommentId, answerType);

                action.setAnswerType(AnswerType.up);
                qaAnswerRepository.save(action);
                return AnswerType.downToUp;
            } else {
                if (actionQuestion) {
                    qaQuestionFieldRepository.downNumIncrement(questionFieldId, 1);
                    qaQuestionFieldRepository.upNumIncrement(questionFieldId, -1);
                } else {
                    qaAnswerRepository.downNumIncrement(actionAnswerOrCommentId, 1);
                    qaAnswerRepository.upNumIncrement(actionAnswerOrCommentId, -1);
                }
                action.setAnswerType(AnswerType.down);
                qaAnswerRepository.save(action);
                return AnswerType.upToDown;
            }
        }
        qaAnswerRepository.save(action);
        return action.getAnswerType();
    }

    public void invitationUserAnswerQuestion(InvitationUserRB invitationUserRB) {
        long questionId = invitationUserRB.getQuestionId();
        if (qaQuestionFieldRepository.userIsCancellation(questionId) > 0) {
            throw new CodeException(CustomerErrorCode.QuestionNotFound);
        }
        LoginUserInfo currentUser = userSupport.getCurrentUser();
        Long userId = currentUser.getId();
        boolean Invited = userNotifyRepository
                .existsByDeletedFalseAndQuestionIdAndCancelFalseAndFromUserIdAndToUserIdAndNotifyType
                        (questionId, userId, invitationUserRB.getUserId(), NotifyType.invitation_user_answer_question);
        if (Invited && !invitationUserRB.isCancel()) {
            throw new CodeException(CustomerErrorCode.TheUserHasBeenInvitedToAnswer);
        }
        if (invitationUserRB.getUserId() == userId) {
            throw new CodeException(CustomerErrorCode.YouCanTInviteYourself);
        }
        if (!userRepository.existsByDeletedFalseAndId(invitationUserRB.getUserId())) {
            throw new CodeException(CustomerErrorCode.UserNotExist);
        }
        Optional<QaQuestionField> questionField = qaQuestionFieldRepository.findByIdAndDeletedFalse(questionId);
        if (questionField.isEmpty()) {
            throw new CodeException(CustomerErrorCode.QuestionNotFound);
        }
        QaQuestionField qaQuestionField = questionField.get();
        if (!qaQuestionField.isAllowAnswer()) {
            throw new CodeException(CustomerErrorCode.QuestionNotFoundOrClose);
        }

        if (invitationUserRB.isCancel() && Invited) {
            UserNotify notify = userNotifyRepository.findByDeletedFalseAndQuestionIdAndCancelFalseAndFromUserIdAndToUserIdAndNotifyType
                    (questionId, userId, invitationUserRB.getUserId(), NotifyType.invitation_user_answer_question);
            notify.setCancel(Invited);
            userNotifyRepository.save(notify);
            return;
        }
        InvitationUserAnswerQuestionMsg msg = new InvitationUserAnswerQuestionMsg();
        msg.setQuestionId(questionId);
        msg.setFormUserId(userId);
        msg.setToUserId(invitationUserRB.getUserId());
        msg.setAnswerTitle(qaQuestionField.getTitle());
        msg.setCancel(invitationUserRB.isCancel());
        rabbitTemplate.convertAndSend
                (UserActiveMQConstants.QUEUE_DDL_USER_INVITATION_USER_ANSWER_QUESTION, msg);

    }

    public boolean acceptedAnswer(long answerId, boolean accepted) {
        Long userId = userSupport.getCurrentUser().getId();
        Long questionId = qaAnswerRepository.getQuestionIdByAnswerId(answerId);
        if (questionId != null) {
            Long userIdByQuestionId = qaQuestionFieldRepository.getUserIdByQuestionId(questionId);

            if (userIdByQuestionId != null) {
                if (userIdByQuestionId.equals(userId)) {
                    qaAnswerRepository.setAcceptState(answerId, accepted);
                    if (accepted) {
                        qaQuestionFieldRepository.setQuestionStateIfNowStateIs
                                (questionId, QuestionState.RESOLVED.ordinal(), QuestionState.HAVE_ANSWER.ordinal());
                    } else {
                        qaQuestionFieldRepository.setQuestionStateIfNowStateIs
                                (questionId, QuestionState.HAVE_ANSWER.ordinal(), QuestionState.RESOLVED.ordinal());
                    }

                    UserQuestionAnswerNotifyMessage message = UserQuestionAnswerNotifyMessage.builder()
                            .formUserId(userId)
                            .answerId(answerId)
                            .userActiveType(UserActiveType.Accepted_Question_Answer)
                            .questionId(questionId)
                            .cancel(false).build();
                    rabbitTemplate.convertAndSend
                            (UserActiveMQConstants.QUEUE_DDL_USER_QUESTION_ANSWER_OR_COMMENT_ACTIVE, message);
                    return true;
                }
            }
        }
        return false;
    }

    public PageData<UserAnswerVO> getUserAnswerPageById(Long userId, PageRequest pageRequest) {
        Page<QaAnswer> answers = qaAnswerRepository.findByDeletedFalseAndUser_IdAndAnswerType(userId, AnswerType.answer, pageRequest);
        ArrayList<UserAnswerVO> userAnswerVOS = new ArrayList<>();
        for (QaAnswer answer : answers) {
            long answerId = answer.getId();
            long questionFieldId = qaQuestionFieldRepository.getIdByAnswerId(answerId);
            String questionTitle = qaQuestionFieldRepository.getTitleByAnswerId(answerId);
            UserAnswerVO build = UserAnswerVO.builder()
                    .id(String.valueOf(answerId))
                    .user(answer.getUser())
                    .upNum(answer.getUpNum())
                    .downNum(answer.getDownNum())
                    .questionTitle(questionTitle)
                    .textPrue(HtmlHelper.toPure(answer.getTextMd()))
                    .accepted(answer.getAccepted())
                    .acceptedTime(answer.getAcceptedTime())
                    .questionFieldId(String.valueOf(questionFieldId))
                    .createTime(answer.getCreateTime()).build();
            userAnswerVOS.add(build);
        }

        return new PageData<>(answers, userAnswerVOS);

    }

    public ArrayList<InvitationUserVO> getRecommendedUserByTagIds(TagIdsRB tagIdsRB, long questionId) {
        R<List<User>> userListR = userTagService.getUserByTagIds(tagIdsRB);
        if (userListR.getCode() != 0) {
            throw new CodeException(CustomerErrorCode.ParamError);
        }
        Long userId = userSupport.getCurrentUser().getId();
        List<User> userList = userListR.getData();
        ArrayList<InvitationUserVO> invitationUserList = new ArrayList<>();
        userList.forEach(user -> {
            boolean Invited = userNotifyRepository
                    .existsByDeletedFalseAndQuestionIdAndCancelFalseAndFromUserIdAndToUserIdAndNotifyType
                            (questionId, userId, user.getId(), NotifyType.invitation_user_answer_question);
            InvitationUserVO invitationUserSearchVO = InvitationUserVO.builder()
                    .userNickName(user.getNickname())
                    .userId(user.getId())
                    .avatar(user.getUserInfo().getAvatar())
                    .Invited(Invited).build();
            invitationUserList.add(invitationUserSearchVO);
        });
        return invitationUserList;
    }

    @NotNull
    public PageData<InvitationUserVO> getInvitationFollowerList(String order, String[] properties, int page, int size, int inviteUid, long questionId) {
        Long userId = userSupport.getCurrentUser().getId();
        R<PageData<User>> userFollowingR = userService.getUserFollower(order, properties, page, size);
        if (userFollowingR.getCode() == 0) {
            throw new CodeException(CustomerErrorCode.ParamError);
        }
        PageData<User> userFollowerPage = userFollowingR.getData();
        List<InvitationUserVO> invitationUserList = new ArrayList<>();
        userFollowerPage.getContent().forEach(user -> {
            boolean Invited = userNotifyRepository
                    .existsByDeletedFalseAndQuestionIdAndCancelFalseAndFromUserIdAndToUserIdAndNotifyType
                            (questionId, userId, inviteUid, NotifyType.invitation_user_answer_question);
            InvitationUserVO invitationUserSearchVO = InvitationUserVO.builder()
                    .userNickName(user.getNickname())
                    .userId(user.getId())
                    .avatar(user.getUserInfo().getAvatar())
                    .Invited(Invited).build();
            invitationUserList.add(invitationUserSearchVO);
        });
        return new PageData<>(userFollowerPage, invitationUserList);
    }

    @NotNull
    public PageData<InvitationUserVO> getInvitationFollowingList(String order, String[] properties, int page, int size, long questionId) {
        Long userId = userSupport.getCurrentUser().getId();
        R<PageData<User>> userFollowingR = userService.getUserFollowing(order, properties, page, size);
        if (userFollowingR.getCode() != 0) {
            throw new CodeException(CustomerErrorCode.ParamError);
        }
        PageData<User> userFollowerPage = userFollowingR.getData();
        List<InvitationUserVO> invitationUserList = new ArrayList<>();
        userFollowerPage.getContent().forEach(user -> {
            boolean Invited = userNotifyRepository
                    .existsByDeletedFalseAndQuestionIdAndCancelFalseAndFromUserIdAndToUserIdAndNotifyType
                            (questionId, userId, user.getId(), NotifyType.invitation_user_answer_question);
            InvitationUserVO invitationUserSearchVO = InvitationUserVO.builder()
                    .userNickName(user.getNickname())
                    .userId(user.getId())
                    .avatar(user.getUserInfo().getAvatar())
                    .Invited(Invited).build();
            invitationUserList.add(invitationUserSearchVO);
        });
        return new PageData<>(userFollowerPage, invitationUserList);
    }


}


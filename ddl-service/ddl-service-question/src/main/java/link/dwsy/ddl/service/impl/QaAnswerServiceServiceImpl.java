package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.XO.Enum.QA.AnswerType;
import link.dwsy.ddl.XO.Enum.User.UserActiveType;
import link.dwsy.ddl.XO.Message.UserCommentNotifyMessage;
import link.dwsy.ddl.XO.RB.QaAnswerRB;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.entity.QA.QaAnswer;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.mq.UserActiveConstants;
import link.dwsy.ddl.repository.QA.QaAnswerRepository;
import link.dwsy.ddl.repository.QA.QaQuestionFieldRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.service.QaAnswerService;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.HtmlHelper;
import link.dwsy.ddl.util.PageData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

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
    private RabbitTemplate rabbitTemplate;

    public PageData<QaAnswer> getByQuestionId(long qid, PageRequest pageRequest) {
        Page<QaAnswer> QaAnswerData = qaAnswerRepository
                .findAllByDeletedIsFalseAndQuestionFieldIdAndParentAnswerId(qid, 0L, pageRequest);
        PageRequest pr = PageRequest.of(0, 8, Sort.by(Sort.Direction.ASC, "createTime"));
        for (QaAnswer qaAnswer : QaAnswerData) {
            long pid = qaAnswer.getId();
            Page<QaAnswer> childQaAnswersPage = qaAnswerRepository
                    .findAllByDeletedIsFalseAndQuestionFieldIdAndParentAnswerId(qid, pid, pr);
            qaAnswer.setChildQaAnswers(childQaAnswersPage.getContent());
            qaAnswer.setChildQaAnswerTotalPages(childQaAnswersPage.getTotalPages());
            qaAnswer.setChildQaAnswerNum(childQaAnswersPage.getTotalElements());
            //添加点赞状态
            LoginUserInfo user = userSupport.getCurrentUser();
            if (user != null) {
                qaAnswerRepository.
                        findByDeletedFalseAndUser_IdAndParentAnswerIdAndAnswerTypeIn
                                (user.getId(), pid,
                                        Set.of(AnswerType.comment, AnswerType.answer,
                                                AnswerType.answer_comment, AnswerType.comment_comment))
                        .ifPresent(c -> qaAnswer.setUserAction(c.getAnswerType()));
                //添加子评论点赞状态
                for (QaAnswer childQaAnswer : childQaAnswersPage.getContent()) {
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

        return new PageData<>(childQaAnswersPage);
    }

    public long answer(QaAnswerRB qaAnswerRB, AnswerType answerType) {

        //todo answer
        long questionFieldId = qaAnswerRB.getQuestionId();

        if (!qaQuestionFieldRepository.existsByDeletedFalseAndAllowAnswerTrueAndId(questionFieldId)) {
            throw new CodeException(CustomerErrorCode.QuestionNotFoundOrClose);
        }
        User user = new User();
        user.setId(userSupport.getCurrentUser().getId());
        QaQuestionField qaQuestionField = new QaQuestionField();
        qaQuestionField.setId(questionFieldId);
        int answerSerialNumber = 1;
        //评论文章
        long parentAnswerId = qaAnswerRB.getParentAnswerId();
        if (parentAnswerId == 0) {

            QaAnswer lastAnswer = qaAnswerRepository
                    .findFirstByDeletedFalseAndQuestionField_IdAndParentAnswerIdAndAnswerTypeOrderByAnswerSerialNumberDesc
                            (questionFieldId, 0L, AnswerType.answer);
            if (lastAnswer != null) {
                answerSerialNumber = lastAnswer.getAnswerSerialNumber() + 1;
            }
            QaAnswer qaAnswer = QaAnswer.builder()
                    .parentAnswerId(0)
                    .parentUserId(0)
                    .textHtml(HtmlHelper.toHTML(qaAnswerRB.getMdText()))
                    .textMd(qaAnswerRB.getMdText())
                    .answerType(answerType)
                    .user(user)
                    .questionField(qaQuestionField)
                    .ua(userSupport.getUserAgent())
                    .answerSerialNumber(answerSerialNumber)
                    .build();

            QaAnswer save = qaAnswerRepository.save(qaAnswer);

            //todo mq notice

            String content = qaAnswer.getTextPure().substring(0, Math.min(100, qaAnswer.getTextPure().length()));

            String title = qaQuestionFieldRepository.getTitle(questionFieldId);
//            if (articleFieldRepository.findUserIdById(articleFieldId)!=user.getId()){
            sendActionMqMessage(user.getId(), questionFieldId, qaAnswerRB.getParentAnswerId(),
                    answerType, false, content, title, save.getId());
//            }
            qaQuestionFieldRepository.answerNumIncrement(questionFieldId, 1);
            return save.getId();
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
                            (questionFieldId, parentAnswerId, AnswerType.answer);
            if (lastAnswer != null) {
                answerSerialNumber = lastAnswer.getAnswerSerialNumber() + 1;
            }
            //回复回答
            qaQuestionFieldRepository.answerNumIncrement(questionFieldId, 1);


            if (qaAnswerRB.getReplyUserAnswerId() == 0) {
                if (userRepository.findById(replyUserId).isEmpty()) {
                    throw new CodeException(CustomerErrorCode.UserNotExist);
                }
                QaAnswer qaAnswer = QaAnswer.builder()
                        .user(user)
                        .questionField(qaQuestionField)
                        .textHtml(HtmlHelper.toHTML(qaAnswerRB.getMdText()))
                        .textMd(qaAnswerRB.getMdText())
                        .parentAnswerId(qaAnswerRB.getParentAnswerId())
                        .parentUserId(replyUserId)
                        .answerType(answerType)
                        .ua(userSupport.getUserAgent())
                        .answerSerialNumber(answerSerialNumber)
                        .build();
                QaAnswer save = qaAnswerRepository.save(qaAnswer);

                String content = qaAnswer.getTextPure().substring(0, Math.min(100, qaAnswer.getTextPure().length()));
                String parentText = qaAnswerRepository.getPureText(qaAnswerRB.getParentAnswerId());
                sendActionMqMessage(user.getId(), questionFieldId, parentAnswerId,
                        answerType, false, content, parentText, save.getId());
                return save.getId();
            } else {
                //回复二级评论
                String replyText;
                replyText = "回复@" + userRepository.findUserNicknameById
                        (qaAnswerRB.getReplyUserId()) + "：" + qaAnswerRB.getMdText();
                QaAnswer qaAnswer = QaAnswer.builder()
                        .user(user)
                        .questionField(qaQuestionField)
                        .textHtml(HtmlHelper.toHTML(qaAnswerRB.getMdText()))
                        .textMd(qaAnswerRB.getMdText())
                        .parentAnswerId(qaAnswerRB.getParentAnswerId())
                        .parentUserId(replyUserId)
                        .answerType(answerType)
                        .replyUserAnswerId(qaAnswerRB.getParentAnswerId())
                        .ua(userSupport.getUserAgent())
                        .answerSerialNumber(answerSerialNumber)
                        .build();

                QaAnswer save = qaAnswerRepository.save(qaAnswer);

                String content = qaAnswer.getTextPure().substring(0, Math.min(100, qaAnswer.getTextPure().length()));
                String parentText = qaAnswerRepository.getPureText(qaAnswerRB.getParentAnswerId());
                sendActionMqMessage(user.getId(), questionFieldId, qaAnswerRB.getReplyUserAnswerId(),
                        answerType, false, content, parentText, save.getId());
                return save.getId();
            }
        }
    }


    public boolean logicallyDelete(long questionId, long answerId) {
        return true;
    }

    private void sendActionMqMessage(long userId, long questionFieldId, long parentAnswerId,
                                     AnswerType answerType, boolean cancel, String formContent, String toContent, long replayAnswerId) {
//        评论
        UserCommentNotifyMessage activeMessage = UserCommentNotifyMessage.builder()
                .userActiveType(UserActiveType.Converter(answerType, parentAnswerId))

                .formUserId(userId)

//                .articleId(articleFieldId)
                .questionId(questionFieldId)
//                .commentId(parentCommentId)
                .answerId(parentAnswerId)
                .ua(userSupport.getUserAgent())


                .cancel(cancel)

                .formContent(formContent)

                .toContent(toContent)

//                .replayCommentId(replayCommentId)

                .replayAnswerId(replayAnswerId)

                .build();
        rabbitTemplate.convertAndSend(UserActiveConstants.QUEUE_DDL_USER_ARTICLE_COMMENT_ACTIVE, activeMessage);
    }



    private void sendActionMqMessage(long userId, long questionFieldId, long parentAnswerId, AnswerType answerType) {
        UserCommentNotifyMessage activeMessage = UserCommentNotifyMessage.builder()
                .userActiveType(UserActiveType.Converter(answerType, parentAnswerId))

                .formUserId(userId)


                .questionId(questionFieldId)

                .answerId(parentAnswerId)

                .ua(userSupport.getUserAgent())

                .cancel(answerType == AnswerType.cancel)

                .build();

        rabbitTemplate.convertAndSend(UserActiveConstants.QUEUE_DDL_USER_ARTICLE_COMMENT_ACTIVE, activeMessage);
    }
}


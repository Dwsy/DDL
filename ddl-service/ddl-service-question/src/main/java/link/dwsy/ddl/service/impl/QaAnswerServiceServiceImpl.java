package link.dwsy.ddl.service.impl;

import link.dwsy.ddl.XO.Enum.QA.AnswerType;
import link.dwsy.ddl.XO.RB.QaAnswerRB;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.core.domain.LoginUserInfo;
import link.dwsy.ddl.entity.QA.QaAnswer;
import link.dwsy.ddl.entity.QA.QaQuestionField;
import link.dwsy.ddl.entity.User.User;
import link.dwsy.ddl.repository.QA.QaAnswerRepository;
import link.dwsy.ddl.repository.QA.QaQuestionFieldRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import link.dwsy.ddl.service.QaAnswerService;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.HtmlHelper;
import link.dwsy.ddl.util.PageData;
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

    public long answer(QaAnswerRB qaAnswerRB, AnswerType answer) {

        //todo answer
        long questionId = qaAnswerRB.getQuestionId();

        if (!qaQuestionFieldRepository.existsByDeletedFalseAndAllowAnswerTrueAndId(questionId)) {
            throw new CodeException(CustomerErrorCode.QuestionNotFoundOrClose);
        }
        User user = new User();
        user.setId(userSupport.getCurrentUser().getId());
        QaQuestionField qaQuestionField = new QaQuestionField();
        qaQuestionField.setId(questionId);
        int answerSerialNumber = 1;
        //评论文章
        long parentAnswerId = qaAnswerRB.getParentAnswerId();
        if (parentAnswerId == 0) {

            QaAnswer lastAnswer = qaAnswerRepository
                    .findFirstByDeletedFalseAndQuestionField_IdAndParentAnswerIdAndAnswerTypeOrderByAnswerSerialNumberDesc
                            (questionId, 0L, AnswerType.answer);
            if (lastAnswer != null) {
                answerSerialNumber = lastAnswer.getAnswerSerialNumber() + 1;
            }
            QaAnswer qaAnswer = QaAnswer.builder()
                    .parentAnswerId(0)
                    .parentUserId(0)
                    .textHtml(HtmlHelper.toHTML(qaAnswerRB.getMdText()))
                    .textMd(qaAnswerRB.getMdText())
                    .answerType(answer)
                    .user(user)
                    .questionField(qaQuestionField)
                    .ua(userSupport.getUserAgent())
                    .answerSerialNumber(answerSerialNumber)
                    .build();

            QaAnswer save = qaAnswerRepository.save(qaAnswer);

            //todo mq notice

//            String content = articleCommentRB.getText().substring(0, Math.min(50, articleCommentRB.getText().length()));
//            Optional<ArticleFieldInfo> t = articleFieldRepository.getTitle(articleFieldId);
//            String title = "";
//            if (t.isPresent()) {
//                title = t.get().getTitle();
//            }

//            if (articleFieldRepository.findUserIdById(articleFieldId)!=user.getId()){
//            sendActionMqMessage(user.getId(), articleFieldId, articleCommentRB.getParentCommentId(),
//                    commentType, false, content, title, save.getId());
//            }
            qaQuestionFieldRepository.answerNumIncrement(questionId, 1);
            return save.getId();
        } else {
            long replyUserId = qaAnswerRB.getReplyUserId();
            if (!qaAnswerRepository.isFirstAnswer(parentAnswerId)) {
                throw new CodeException(CustomerErrorCode.ArticleCommentNotIsFirst);//todo
            }

            if (!qaAnswerRepository
                    .existsByDeletedFalseAndIdAndQuestionField_IdAndAnswerType(parentAnswerId, questionId, AnswerType.answer)) {
                throw new CodeException(CustomerErrorCode.ArticleCommentNotFount);
            }


            QaAnswer lastAnswer = qaAnswerRepository
                    .findFirstByDeletedFalseAndQuestionField_IdAndParentAnswerIdAndAnswerTypeOrderByAnswerSerialNumberDesc
                            (questionId, parentAnswerId, AnswerType.answer);
            if (lastAnswer != null) {
                answerSerialNumber = lastAnswer.getAnswerSerialNumber() + 1;
            }
            //回复回答
            qaQuestionFieldRepository.answerNumIncrement(questionId, 1);


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
                        .answerType(answer)
                        .ua(userSupport.getUserAgent())
                        .answerSerialNumber(answerSerialNumber)
                        .build();
                QaAnswer save = qaAnswerRepository.save(qaAnswer);
//                String content = articleCommentRB.getText().substring(0, Math.min(100, articleCommentRB.getText().length()));
//                String parentText = articleCommentRepository.getText(articleCommentRB.getParentCommentId());
//                sendActionMqMessage(user.getId(), articleFieldId, articleCommentRB.getParentCommentId(),
//                        commentType, false, content, parentText, save.getId());
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
                        .answerType(answer)
                        .replyUserAnswerId(qaAnswerRB.getParentAnswerId())
                        .ua(userSupport.getUserAgent())
                        .answerSerialNumber(answerSerialNumber)
                        .build();


//                String content = articleCommentRB.getText().substring(0, Math.min(100, articleCommentRB.getText().length()));
//                String parentText = articleCommentRepository.getText(articleCommentRB.getReplyUserCommentId());

                QaAnswer save = qaAnswerRepository.save(qaAnswer);

//                ArticleComment save = articleCommentRepository.save(articleComment);
//                sendActionMqMessage(user.getId(), articleFieldId, articleCommentRB.getReplyUserCommentId(),
//                        commentType, false, content, parentText, save.getId());
                return save.getId();
            }
        }
    }


    public boolean logicallyDelete(long questionId, long answerId) {
        return true;
    }
}

